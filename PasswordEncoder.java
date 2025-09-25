import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoder {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        System.out.println("Generating BCrypt hashes for passwords:");
        System.out.println("admin123: " + encoder.encode("admin123"));
        System.out.println("password123: " + encoder.encode("password123"));
    }
}