import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Wallet {
    public PrivateKey privateKey; // sign transactions
    public PublicKey publicKey; // address

    public HashMap<String, TransactionOutput> UTXOs = new HashMap<>();

    public Wallet() {
        generateKeyPair();
    }

    public void generateKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA", "BC");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            ECGenParameterSpec ecParameterSpec = new ECGenParameterSpec("prime192v1");
            keyGen.initialize(ecParameterSpec, random);
            KeyPair keyPair = keyGen.generateKeyPair();
            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public float getBalance() {
        float total = 0;
        for (Map.Entry<String, TransactionOutput> item : Blockchain.UTXOs.entrySet()) {
            TransactionOutput UTXO = item.getValue();
            if (UTXO.isMine(publicKey)) {
                UTXOs.put(UTXO.id, UTXO);
                total += UTXO.amount;
            }
        }
        return total;
    }

    public Transaction sendFunds(PublicKey _recipient, float amount) {
        if (getBalance() < amount) {
            System.out.println("#Not enough funds to complete the transaction. Transaction cancelled.");
            return null;
        }

        ArrayList<TransactionInput> inputs = new ArrayList<>();

        float total = 0;
        for (Map.Entry<String, TransactionOutput> item : UTXOs.entrySet()) {
            TransactionOutput UTXO = item.getValue();
            total += UTXO.amount;
            inputs.add(new TransactionInput(UTXO.id));
            if (total > amount)
                break;
        }

        Transaction newTransaction = new Transaction(publicKey, _recipient, amount, inputs);
        newTransaction.generateSignature(privateKey);

        for (TransactionInput input : inputs)
            UTXOs.remove(input.transactionOutputId);
        return newTransaction;
    }
}
