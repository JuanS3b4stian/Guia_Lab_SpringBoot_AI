import { useCallback, useEffect, useState } from "react";
import transactionService from "../../services/TransactionService";
import type { TransactionResponse } from "../../types/transaction";
import "./TransactionHistory.css";

/**
 * Displays the transaction history and allows completed transfers to be reverted.
 */
export function TransactionHistory() {
  const [transactions, setTransactions] = useState<TransactionResponse[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [revertingTransactionId, setRevertingTransactionId] = useState<number | null>(
    null,
  );

  const loadTransactions = useCallback(async () => {
    setIsLoading(true);
    setErrorMessage(null);

    try {
      const history = await transactionService.getAllTransactions();
      setTransactions(history);
    } catch {
      setErrorMessage("No se pudo cargar el historial de transacciones.");
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    void loadTransactions();
  }, [loadTransactions]);

  const handleRevert = async (transactionId: number) => {
    if (!window.confirm("¿Deseas revertir esta transferencia?")) {
      return;
    }

    setRevertingTransactionId(transactionId);
    setErrorMessage(null);

    try {
      await transactionService.revertTransaction(transactionId);
      setTransactions((currentTransactions) =>
        currentTransactions.map((transaction) =>
          transaction.id === transactionId
            ? { ...transaction, status: "FAILED", qrCodeBase64: null }
            : transaction,
        ),
      );
    } catch {
      setErrorMessage("No se pudo revertir la transferencia.");
    } finally {
      setRevertingTransactionId(null);
    }
  };

  if (isLoading) {
    return <p className="transaction-history__message">Cargando transacciones...</p>;
  }

  return (
    <section className="transaction-history" aria-labelledby="transaction-history-title">
      <div className="transaction-history__header">
        <h2 id="transaction-history-title">Historial de transacciones</h2>
        <button type="button" onClick={() => void loadTransactions()}>
          Actualizar
        </button>
      </div>

      {errorMessage && (
        <p className="transaction-history__error" role="alert">
          {errorMessage}
        </p>
      )}

      {transactions.length === 0 ? (
        <p className="transaction-history__message">
          No hay transacciones registradas.
        </p>
      ) : (
        <div className="transaction-history__table-wrapper">
          <table className="transaction-history__table">
            <thead>
              <tr>
                <th scope="col">ID</th>
                <th scope="col">Cuenta origen</th>
                <th scope="col">Cuenta destino</th>
                <th scope="col">Monto</th>
                <th scope="col">Fecha</th>
                <th scope="col">Estado</th>
                <th scope="col">Acciones</th>
              </tr>
            </thead>
            <tbody>
              {transactions.map((transaction) => (
                <tr key={transaction.id}>
                  <td>{transaction.id}</td>
                  <td>{transaction.sourceAccountId}</td>
                  <td>{transaction.destinationAccountId}</td>
                  <td>{transaction.amount.toLocaleString("es-CO")}</td>
                  <td>{new Date(transaction.transactionDate).toLocaleString("es-CO")}</td>
                  <td>
                    <span
                      className={`transaction-history__status transaction-history__status--${transaction.status.toLowerCase()}`}
                    >
                      {transaction.status}
                    </span>
                  </td>
                  <td>
                    <button
                      type="button"
                      disabled={
                        transaction.status !== "COMPLETED" ||
                        revertingTransactionId === transaction.id
                      }
                      onClick={() => void handleRevert(transaction.id)}
                    >
                      {revertingTransactionId === transaction.id
                        ? "Revirtiendo..."
                        : "Revertir"}
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </section>
  );
}
