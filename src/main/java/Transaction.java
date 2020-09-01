import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

public class Transaction {
    public String transactionId; // hash of the transaction
    public PublicKey sender; // address of the sender
    public PublicKey recipient; // address of the recipient
    public float amount; // amount of funds involved in the transaction
    public byte[] signature; // prevents others from spending funds from our wallet

    public ArrayList<TransactionInput> inputs;
    public ArrayList<TransactionOutput> outputs = new ArrayList<>();

    private static int count = 0;

    public Transaction(PublicKey from, PublicKey to, float amount, ArrayList<TransactionInput> inputs) {
        this.sender = from;
        this.recipient = to;
        this.amount = amount;
        this.inputs = inputs;
    }

    private String calculateHash() {
        count++; // increment to avoid two transactions having the same hash
        return StringUtil.applySha256(StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(recipient) + amount + count);
    }

    public void generateSignature(PrivateKey privateKey) {
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(recipient) + amount;
        signature = StringUtil.applyECDSASignature(privateKey, data);
    }

    public boolean verifySignature() {
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(recipient) + amount;
        return StringUtil.verifyECDSASignature(sender, data, signature);
    }

    public boolean processTransaction() {
        if (!verifySignature()) {
            System.out.println("#Failed to verify transaction signature");
            return false;
        }

        // checking transaction inputs are unspent
        for (TransactionInput i : inputs) {
            i.UTXO = Blockchain.UTXOs.get(i.transactionOutputId);
        }

        // checking the transaction is big enough
        if (getInputsValue() < Blockchain.minimumTransaction) {
            System.out.println("#Transaction input does not meet the minimum transaction size criteria: " + getInputsValue());
            return false;
        }

        // generating transaction outputs
        float leftOver = getInputsValue() - amount;
        transactionId = calculateHash();
        outputs.add(new TransactionOutput(this.recipient, amount, transactionId));
        outputs.add(new TransactionOutput(this.sender, leftOver, transactionId));

        // adding outputs to unspent list
        for (TransactionOutput o : outputs) {
            Blockchain.UTXOs.put(o.id, o);
        }

        // removing inputs from unspent list
        for (TransactionInput i : inputs) {
            if (i.UTXO == null)
                continue;
            Blockchain.UTXOs.remove(i.UTXO.id);
        }

        return true;
    }

    public float getInputsValue() {
        float total = 0;
        for (TransactionInput i : inputs) {
            if (i.UTXO == null)
                continue;
            total += i.UTXO.amount;
        }
        return total;
    }

    public float getOutputValue() {
        float total = 0;
        for (TransactionOutput o : outputs)
            total += o.amount;
        return total;
    }

}
