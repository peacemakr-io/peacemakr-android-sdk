<p align="center">
  <br>
    <img src="https://admin.peacemakr.io/p_logo.png" width="150"/>
  <br>
</p>

# Peacemakr E2E-Encryption-as-a-Service Android SDK

Peacemakr's E2E-Encryption-as-a-Service SDK simplifies your data security with E2E-Encryption service and automated key lifecycle management.

You can easily encrypt your data without worrying about backward compatibility, cross platform portability, or changing security requirements.

Our Zero-Trust capability allows you to customize your security strength to meet the highest standard without having to place your trust in Peacemakr as we don’t have the capacity to get your keys and decrypt your data.

## License

The content of this SDK is open source under [Apache License 2.0](https://github.com/peacemakr-io/peacemakr-android-sdk/blob/master/LICENSE).


## Quick Start, Integrate this SDK

- Navigate to the latest release.
- Download the `aar`'s from the release tab.
- Include the aar's in your project's `CLASSPATH`
- Update your build to pull in the java-sdk, but without the unix native core crypto
   - For example,
   ```java
   implementation fileTree(dir: "libs", include: ["PeacemakrCoreCrypto-0.0.2.aar"])
    implementation ('io.peacemakr:peacemakr-java-sdk:0.0.2') {
        exclude group: 'io.peacemakr', module: 'peacemakr-core-crypto'
    }
    ```
- Obtain your APIKey, using your admin poral (https://admin.peacemakr.io).
- Construct a new instance of the Peacemakr Java SDK, using your APIKey,
   - `ICrypto peacemakrSDK = Factory.getCryptoSDK(myAPIKey, "my client name", null, new FilePersister("~/.peacemakr"), null);`
- Start Encrypting and Decrypting, for example,
   - `byte[] encrypted = peacemakrSDK.encrypt(plaintext);`
   - `byte[] decrypted = peacemakrSDK.decrypt(encrypted);`

## Example Integration
 - See `example` folder for a encryption / decryption sample app.

```
class SimpleEncryptDecrypt {
    public static void main(String[] args) throws Exception {

        String apiKey = "your-api-key";
        InMemoryPersister persister = new InMemoryPersister();

      ICrypto cryptoI = Factory.getCryptoSDK(apiKey, "simple encrypt decrypt", null, persister, null);
      cryptoI.register();

      String plaintext = "Hello world!";

      byte[] encrypted = cryptoI.encrypt(plaintext.getBytes());
      System.out.println("Encrypted: " + new String(encrypted));

      byte[] decrypted = cryptoI.decrypt(encrypted);
        System.out.println("Decrypted: " + new String(decrypted));
    }
}
```

## Integration Details

 - The Facotry, and constructing a client.
```
    /**
     *
     * This factory constructs a Peacemakr SDK Client.  All Peacemakr SDK clients
     * implement the ICrypto interface.
     *
     * All clients are stateful.  Internally, this state includes a private asymmetric
     * key, local cache of symmetric keys downloaded from Peacemakr so far, ability to
     * communicate with the peacemakr service org. All state is persisted through
     * your provided Persister. This mechanism allows for a single client to re-use
     * a previously registered api client (and not incur additional overhead due to
     * re-registering the same client over and over again).
     *
     * Auth is handled through the provided apiKey. If you do not have one, please register
     * at https://admin.peacemakr.io as a new organization. If you have a peacemakr organization
     * already, but are not sure what your apiKey should be, please login
     * (https://admin.peacemakr.io) and navigate to "API Keys" tab, and select one of your apiKey's.
     * The same API Key may be re-used across different clients.
     *
     * Persisting local data is important features of Peacemakr Clients. To help make this
     * as easy and seamless as possible, this client will only ever read or write through
     * this simple provided interface. There are two implementations of this interface which
     * are already provided: FilePersister and InMemoryPersister.  They do exactly as their
     * names describe. If your specific application requires a different or special type of
     * persistence layer, you are welcomed to implement this interface in whichever fashion
     * best suites your needs, and even open a PR against our SDK to ensure that we continue
     * supporting your particular persistenc layer it as we update and improve the clients.
     *
     *
     * @param apiKey Required. Auth mechanism which permits this client to connect to your Peacemakr Organization.
     * @param clientName Required. Any string which may be used to identify this particular client.  Please do not use
     *                   any customer Personally Identifiable Information (PII) in this field.
     * @param peacemakrBaseURL Optional. The base url for Peacemakr's  Cloud Services. If null, the default value
     *                        (https://api.peacemakr.io) is used.
     * @param persister Required. This persister help the cleint persist data.
     * @param logger Optional. If null, we use a standard log4j logger, else, you are welcomed to provide your own
     *               logger solution for local visibility and debugging.
     * @return An ICrypto which is ready to be used.
     * @throws PeacemakrException Is thrown on any non-recoverable error.
     */
    public static ICrypto getCryptoSDK(String apiKey, String clientName, String peacemakrBaseURL, Persister persister, Logger logger) throws PeacemakrException;
```

  - The interface in this SDK for Application Layer Cryptography:
```
public interface ICrypto {
  /**
   * Registers to PeaceMakr as a client. The persister is used to detect prior registrations on this client, so safe
   * to call multiple times. Once a successful invocation of Register is executed once, subsequent calls become a
   * noop. One successful call is required before any cryptographic use of this SDK.
   * 
   * Registration may fail with invalid apiKey, missing network connectivity, or an invalid persister. On failure,
   * take corrections action and invoke again.
   */
  void register() throws PeacemakrException;

  /**
   * Sync all available keys for this client. This invocation will help performance of subsequent encryption
   * and decryption calls.
   * 
   * Sync may fail, if registration was not invoked, if there's network connectivity issues, or
   * unexpected authorization issues.
   */
  void sync() throws PeacemakrException;

  /**
   * Encrypt the plaintext, using a random available usedomain.
   *
   * @param plainText Plaintext bytes to encrypt.
   * @return Opaquely packaged ciphertext.
   * @throws PeacemakrException On any error (network connectivity issues, authN issues, etc)
   */
  byte[] encrypt(byte[] plainText) throws PeacemakrException;

  /**
   * Encrypt the plaintext, but restrict which keys may be used to a Use Domain of this specific name. Names of Use
   * Domains are not unique, and this non-unique property of your Organization's Use Domains allows for graceful
   * rotation of encryption keys off of old (retiring, stale, or compromised) Use Domains, simply by creating a new
   * Use Domain with the same name. The transitional purity, both Use Domains may be selected for encryption use by
   * clients restricted to one particular name. Then, retiring of one of the two Use Domains is possible without
   * disrupting your deployed application.
   *
   * @param plainText     Plaintext to encrypt.
   * @param useDomainName Non-unique User Domain of your organization's.
   */
  byte[] encryptInDomain(byte[] plainText, String useDomainName) throws PeacemakrException, UnsupportedEncodingException;

  /**
   * Decrypt the opaquely packaged ciphertext and return the original plain text.
   *
   * @param cipherText CipherText to decrypt.
   */
  byte[] decrypt(byte[] cipherText) throws PeacemakrException;

  /**
   * For visibility or debugging purposes, returns a string whihc identifies which
   * client and configuration this client is running.
   */
  String getDebugInfo();
}
```

## Contributions

Peacemakr welcomes open and active contributions to this SDK. As long as they're in the spirit of project, we will most likely accept them. However, you may want to get our opinion on proposed changes before investing time, so we can work together to solve problems you encounter that make sense for the future direction we have planned.

## Testing

We use the usual fork and PR mechanisms, and in this section, here are some basic guidelines on how to setup a development environment. Without being a member of peacemakr, you will not have full access to the testing infrastructure required for complete code coverage, but our CircleCI build and test pipeline can be used to provide this level of visibility and provide feedback.

## Development Environment

For the SDK, please see https://github.com/peacemakr-io/peacemakr-java-sdk.

For the example android app, please see example-android.
