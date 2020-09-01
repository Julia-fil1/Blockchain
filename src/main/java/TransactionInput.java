public class TransactionInput {
    public String transactionOutputId;
    public TransactionOutput UTXO; // unspent transaction output

    public TransactionInput(String transactionOutputId) {
        this.transactionOutputId = transactionOutputId;
    }
}
