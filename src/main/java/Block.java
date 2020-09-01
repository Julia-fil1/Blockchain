import java.util.ArrayList;
import java.util.Date;

public class Block {
    public String hash;
    public String previousHash;
    public String merkleRoot;
    public ArrayList<Transaction> transactions = new ArrayList<>();
    private final long timeStamp = new Date().getTime(); //no. of milliseconds since 1/1/1970
    private int nonce;

    public Block(String previousHash) {
        this.previousHash = previousHash;
        this.hash = calculateHash();
    }

    public String calculateHash() {
        return StringUtil.applySha256(previousHash + timeStamp + nonce + merkleRoot);
    }

    public void mineBlock(int difficulty) {
        merkleRoot = StringUtil.getMerkleRoot(transactions);
        String target = StringUtil.getDifficultyString(difficulty);
        while (!hash.substring(0, difficulty).equals(target)) {
            nonce++;
            hash = calculateHash();
        }
        System.out.println("The block has been mined: " + hash);
    }

    public boolean addTransaction(Transaction transaction) {
        if (transaction == null)
            return false;
        if (!previousHash.equals("0")) {
            if (!transaction.processTransaction()) {
                System.out.println("Transaction failed to process. Discarded.");
                return false;
            }
        }
        transactions.add(transaction);
        System.out.println("Transaction successfully added to block");
        return true;
    }
}
