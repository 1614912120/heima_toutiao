import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.util.DigestUtils;

/**
 * ClassName: test
 * Package: PACKAGE_NAME
 * Description:
 *
 * @Author R
 * @Create 2023/12/28 14:24
 * @Version 1.0
 */
public class test {
    @Test
    public void md5() {
        String salt = RandomStringUtils.randomAlphabetic(10);
        System.out.println(salt);
        String s = DigestUtils.md5DigestAsHex(("helloSNNKJBknzv").getBytes());
        System.out.println(s);
    }
}
