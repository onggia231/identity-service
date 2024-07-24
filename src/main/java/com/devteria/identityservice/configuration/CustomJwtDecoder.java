package com.devteria.identityservice.configuration;

import com.devteria.identityservice.dto.request.IntrospectRequest;
import com.devteria.identityservice.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.text.ParseException;
import java.util.Objects;

@Component // Nhu danh dau day la 1 Bean de quan ly
public class CustomJwtDecoder implements JwtDecoder { // JwtDecoder: giải mã và xác thực JSON Web Tokens (JWT).
    @Value("${jwt.signerKey}")
    private String signerKey;

    // Dùng @Autowired khi bạn muốn Spring tự động tiêm phụ thuộc vào các bean của bạn (Không cần khoi tao ham constructor de khai tham so authenticationService)
    // Nếu không sử dụng @Autowired thì khai báo kiểu Lombok với @RequiredArgsConstructor
    @Autowired
    private AuthenticationService authenticationService;

    private NimbusJwtDecoder nimbusJwtDecoder = null; // một lớp trong Spring Security OAuth 2.0 được sử dụng để giải mã và xác thực JSON Web Tokens (JWT)

    // Phương thức decode được triển khai từ giao diện JwtDecoder, có trách nhiệm giải mã token JWT
    @Override
    public Jwt decode(String token) throws JwtException {

        try {
            // Kiểm tra token có hợp lệ không
            var response = authenticationService.introspect(
                    IntrospectRequest.builder().token(token).build());

            if (!response.isValid()) throw new JwtException("Token invalid");
        } catch (JOSEException | ParseException e) {
            throw new JwtException(e.getMessage());
        }

        // Nếu nimbusJwtDecoder chưa được khởi tạo (Objects.isNull(nimbusJwtDecoder)),
        // nó được cấu hình với một khóa bí mật (signerKey) và thuật toán HMAC-SHA-512 (HS512)

        if (Objects.isNull(nimbusJwtDecoder)) {
            SecretKeySpec secretKeySpec = new SecretKeySpec(signerKey.getBytes(), "HS512");
            nimbusJwtDecoder = NimbusJwtDecoder.withSecretKey(secretKeySpec)
                    .macAlgorithm(MacAlgorithm.HS512)
                    .build();
        }

        return nimbusJwtDecoder.decode(token);
    }
}

/*
Nhiệm Vụ:
    Giải Mã JWT: CustomJwtDecoder thực hiện nhiệm vụ giải mã JSON Web Token (JWT) để kiểm tra tính hợp lệ của token và giải mã các thông tin trong đó.
Luồng Hoạt Động:
    Kiểm Tra Token: Sử dụng introspect để xác nhận token có hợp lệ không.
    Tạo NimbusJwtDecoder: Nếu chưa có NimbusJwtDecoder, nó sẽ tạo một cái mới với khóa bí mật.
    Giải Mã Token: Sử dụng NimbusJwtDecoder để giải mã token và kiểm tra thông tin.
*/
