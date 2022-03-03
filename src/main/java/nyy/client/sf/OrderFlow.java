package nyy.client.sf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Properties;

import static javax.crypto.Cipher.DECRYPT_MODE;

public class OrderFlow {

  public static void main(String[] args) throws Exception {
    new OrderFlow().createVault();
  }

  private final RestTemplate restTemplate;
  private final ObjectMapper mapper = new ObjectMapper();

  private final String host = "https://sf.sit.jfl.nowyoyo.net";
  private final String preSharedKey = "SIT_TEST_KEY";
  private final String oneTimeKey;

  OrderFlow() throws IOException {
    RestTemplateBuilder builder = new RestTemplateBuilder();
    try (final InputStream stream = this.getClass().getResourceAsStream("/auth.properties")) {

      Properties prop = new Properties();

      prop.load(stream);
      restTemplate = builder.basicAuthentication(prop.getProperty("username"), prop.getProperty("password")).build();
    }

    this.oneTimeKey = "8hw4bgni3ut97477f";
  }


  private void createVault() throws GeneralSecurityException, IOException {
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-ONETIME-KEY", this.oneTimeKey);
    headers.setContentType(MediaType.APPLICATION_JSON);
    ResponseEntity<ObjectNode> objectNodeResponseEntity = restTemplate.exchange(
            host + "/R2/api/mandate/braintree/new-vault/client-authorisation-id",
            HttpMethod.GET,
            new HttpEntity<>(headers),
            ObjectNode.class
    );

    ObjectNode body = objectNodeResponseEntity.getBody();

    // If getting client ID from existing account this would be
    // byte[] key = createKeyFromSecret(jflAccountId + this.preSharedKey);
    // i.e. createKeyFromSecret("A12344" + "SIT_TEST_KEY");
    byte[] key = createKeyFromSecret(this.oneTimeKey + this.preSharedKey);
    byte[] decryptedData = decryptWithAESCBC(Base64.getDecoder().decode(body.get("encrypted_data").asText()), key, Base64.getDecoder().decode(body.get("iv").asText()));
    ObjectNode clientTokenObject = (ObjectNode) mapper.readTree(decryptedData);

    String clientToken = clientTokenObject.get("client_token").asText();

    System.out.println("* Billing Profile ID : " + objectNodeResponseEntity.getHeaders().getFirst("X-BILLING-PROFILE-ID"));
    System.out.println("* Client Token : " + clientToken);
  }

  private static byte[] createKeyFromSecret(String secret) throws NoSuchAlgorithmException {
    MessageDigest sha = MessageDigest.getInstance("SHA-512");
    return Arrays.copyOf(sha.digest(secret.getBytes()), 16);
  }

  private byte[] decryptWithAESCBC(byte[] encryptedData, byte[] key, byte[] iv) throws GeneralSecurityException {
    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    cipher.init(DECRYPT_MODE, new SecretKeySpec(key, "AES"), new IvParameterSpec(iv));
    return cipher.doFinal(encryptedData);
  }

}
