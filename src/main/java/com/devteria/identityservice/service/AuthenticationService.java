package com.devteria.identityservice.service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.devteria.identityservice.dto.request.AuthenticationRequest;
import com.devteria.identityservice.dto.request.IntrospectRequest;
import com.devteria.identityservice.dto.request.LogoutRequest;
import com.devteria.identityservice.dto.request.RefreshRequest;
import com.devteria.identityservice.dto.response.AuthenticationResponse;
import com.devteria.identityservice.dto.response.IntrospectResponse;
import com.devteria.identityservice.entity.InvalidatedToken;
import com.devteria.identityservice.entity.User;
import com.devteria.identityservice.exception.AppException;
import com.devteria.identityservice.exception.ErrorCode;
import com.devteria.identityservice.repository.InvalidatedTokenRepository;
import com.devteria.identityservice.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
// Chỉ tạo constructor cho các trường final và @NonNull. Các trường không final hoặc không @NonNull sẽ không được bao
// gồm trong constructor này
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    UserRepository userRepository;
    InvalidatedTokenRepository invalidatedTokenRepository;

    @NonFinal // @NonFinal khong inject vao Contructor
    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;

    @NonFinal
    @Value("${jwt.valid-duration}")
    protected long VALID_DURATION;

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    protected long REFRESHABLE_DURATION;

    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
        var token = request.getToken();
        boolean isValid = true;

        // Doan comment off-on kia spotless se khong fomat code
        // mvn spotless:apply de chay spotless
        // spotless:off
        try {
            verifyToken(token, false); // false: ko refresh token chi kiem tra token
        } catch (AppException e) {
            isValid = false;
        }
        //spotless:on

        return IntrospectResponse.builder().valid(isValid).build();
    }

    // check authentication xem username va password dung chua moi cho generateToken
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        // check username co trong db khong
        var user = userRepository
                .findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        // check mat khau user da ma hoa va mat khau request xem co matches
        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!authenticated) throw new AppException(ErrorCode.UNAUTHENTICATED);

        // Neu username, password dung thi generate token
        var token = generateToken(user);
        return AuthenticationResponse.builder().token(token).authenticated(true).build();
    }

    public void logout(LogoutRequest request) throws ParseException, JOSEException {
        try {
            var signToken = verifyToken(request.getToken(), true);

            String jit = signToken.getJWTClaimsSet().getJWTID(); // JWT ID là một giá trị duy nhất xác định token này
            Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime(); // Thời gian hết hạn của token

            InvalidatedToken invalidatedToken =
                    InvalidatedToken.builder().id(jit).expiryTime(expiryTime).build();

            invalidatedTokenRepository.save(invalidatedToken); // lưu trữ thông tin của token đã bị vô hiệu hóa
        } catch (AppException exception) {
            log.info("Token already expired");
        }
    }

    // verified token check xem token da bi sua doi hay het han chua hay ton tai trong bang InvalidatedToken
    private SignedJWT verifyToken(String token, boolean isRefresh) throws ParseException, JOSEException {
        JWSVerifier verifier =
                new MACVerifier(SIGNER_KEY.getBytes()); // Đây là bước quan trọng để xác minh chữ ký của token

        SignedJWT signedJWT = SignedJWT.parse(
                token); // Chuyển đổi chuỗi token thành một đối tượng SignedJWT để dễ dàng truy cập các phần của token
        // như claims, chữ ký, v.v.
        // check thoi gian expityTime
        // isRefresh = true -> verify de refresh token (REFRESHABLE_DURATION de them thoi gian cho token)
        // isRefresh = false -> verify de cho authentication or introspect ( dung thoi gian het han duoc chi dinh trong
        // token)
        Date expityTime = (isRefresh)
                ? new Date(signedJWT
                        .getJWTClaimsSet()
                        .getIssueTime()
                        .toInstant()
                        .plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS)
                        .toEpochMilli())
                : signedJWT.getJWTClaimsSet().getExpirationTime();
        // verified token check xem token da bi sua doi hay het han chua
        var verified = signedJWT.verify(verifier);
        // kiem tra xem het thoi gian token va valid token chuan chua
        if (!(verified && expityTime.after(new Date()))) throw new AppException(ErrorCode.UNAUTHENTICATED);

        // Kiểm tra xem token có tồn tại trong invalidatedTokenRepository (tức là đã bị đánh dấu là không hợp lệ) hay
        // không
        if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        return signedJWT;
    }

    // Khi ma gan het han token se goi api refreshToken de sinh ra 1 token moi, khi ay dung token moi de tiep tuc su
    // dung
    public AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException {
        // - kiem tra hieu luc cua token
        var signedJWT = verifyToken(request.getToken(), true);
        var jit = signedJWT.getJWTClaimsSet().getJWTID();
        var expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        // dua token vao bang invalidated_token duoc xem la logout
        InvalidatedToken invalidatedToken =
                InvalidatedToken.builder().id(jit).expiryTime(expiryTime).build();

        invalidatedTokenRepository.save(invalidatedToken);

        // get username
        var username = signedJWT.getJWTClaimsSet().getSubject();
        var user =
                userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));
        // tao token moi
        var token = generateToken(user);
        return AuthenticationResponse.builder().token(token).authenticated(true).build();
    }

    private String generateToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512); // Header chứa thông tin về thuật toán sử dụng để ký token

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("namngoc.com")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(user))
                .build();

        Payload payload = new Payload(
                jwtClaimsSet.toJSONObject()); // Chuyển đổi các claim thành đối tượng JSONObject và tạo payload từ đó

        JWSObject jwsObject = new JWSObject(header, payload); // Tạo một đối tượng JWSObject với header và payload.

        // Truy cap duong dan ben duoi tao ma
        // https://generate-random.org/encryption-key-generator?count=1&bytes=32&cipher=aes-256-cbc&string=&password=
        // Truy cap duong dan ben duoi xem token gen ra
        // https://jwt.io/
        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot create token", e);
            throw new RuntimeException(e);
        }
    }

    private String buildScope(User user) {
        StringJoiner stringJoiner =
                new StringJoiner(" "); // vi oauth2 phan cach nhau bang dau cach, để nối các phần tử.

        if (!CollectionUtils.isEmpty(user.getRoles())) // check role
        user.getRoles().forEach(role -> {
            stringJoiner.add(
                    "ROLE_" + role.getName()); // custom thay vi dung mac dinh SCOPE_ thi dung ROLE_ cho quen thuoc
            if (!CollectionUtils.isEmpty(role.getPermissions())) // check permissions
            role.getPermissions().forEach(permission -> stringJoiner.add(permission.getName()));
        });
        return stringJoiner.toString();
    }
}
