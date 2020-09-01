import java.security.PublicKey;

public class TransactionOutput {
    public String id;
    public PublicKey recipient;
    public float amount;
    public String parentTransactionId;

    public TransactionOutput(PublicKey recipient, float amount, String parentTransactionId) {
        this.recipient = recipient;
        this.amount = amount;
        this.parentTransactionId = parentTransactionId;
        this.id = StringUtil.applySha256(StringUtil.getStringFromKey(recipient) + amount + parentTransactionId);
    }

    public boolean isMine(PublicKey publicKey) {
        return (publicKey == recipient);
    }
}
