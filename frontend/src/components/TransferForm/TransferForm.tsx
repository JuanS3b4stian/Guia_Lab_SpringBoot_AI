import { FormEvent, useEffect, useState } from "react";
import axios from "axios";
import { QRCodeCanvas } from "qrcode.react";
import transactionService from "../../services/TransactionService";
import type {
  AccountResponse,
  TransactionRequest,
  TransactionResponse,
} from "../../types/transaction";
import "./TransferForm.css";

interface TransferFormState {
  sourceAccountId: string;
  destinationAccountId: string;
  amount: string;
}

const initialFormState: TransferFormState = {
  sourceAccountId: "",
  destinationAccountId: "",
  amount: "",
};

/**
 * Renders the form used to create a money transfer and display its QR code.
 */
export function TransferForm() {
  const [formState, setFormState] = useState<TransferFormState>(initialFormState);
  const [transaction, setTransaction] = useState<TransactionResponse | null>(null);
  const [accounts, setAccounts] = useState<AccountResponse[]>([]);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isLoadingAccounts, setIsLoadingAccounts] = useState(true);

  useEffect(() => {
    const loadAccounts = async () => {
      try {
        setAccounts(await transactionService.getAllAccounts());
      } catch (error) {
        const backendMessage = axios.isAxiosError(error)
          ? error.response?.data?.error ?? error.message
          : "No se pudieron cargar las cuentas.";
        setErrorMessage(backendMessage);
      } finally {
        setIsLoadingAccounts(false);
      }
    };

    void loadAccounts();
  }, []);

  const handleChange = (field: keyof TransferFormState, value: string) => {
    setFormState((currentState) => ({
      ...currentState,
      [field]: value,
    }));
  };

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setErrorMessage(null);
    setTransaction(null);
    setIsSubmitting(true);

    const request: TransactionRequest = {
      sourceAccountId: Number(formState.sourceAccountId),
      destinationAccountId: Number(formState.destinationAccountId),
      amount: Number(formState.amount),
    };

    try {
      const createdTransaction = await transactionService.createTransaction(request);
      setTransaction(createdTransaction);
      setFormState(initialFormState);
    } catch (error) {
      const backendMessage = axios.isAxiosError(error)
        ? error.response?.data?.error ?? error.message
        : error instanceof Error
          ? error.message
          : "Error desconocido";
      setErrorMessage(`No se pudo realizar la transferencia: ${backendMessage}`);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <section className="transfer-form" aria-labelledby="transfer-form-title">
      <h2 id="transfer-form-title">Nueva transferencia</h2>

      {errorMessage && (
        <div className="transfer-form__toast transfer-form__toast--error" role="alert">
          {errorMessage}
          <button type="button" onClick={() => setErrorMessage(null)} aria-label="Cerrar alerta">
            ×
          </button>
        </div>
      )}

      <form className="transfer-form__fields" onSubmit={handleSubmit}>
        <label htmlFor="source-account">Cuenta origen</label>
        <select
          id="source-account"
          name="sourceAccountId"
          required
          value={formState.sourceAccountId}
          onChange={(event) => handleChange("sourceAccountId", event.target.value)}
          disabled={isLoadingAccounts || isSubmitting}
        >
          <option value="">Selecciona una cuenta</option>
          {accounts.map((account) => (
            <option key={account.id} value={account.id}>
              {account.accountNumber} - {account.ownerName} (Saldo: {account.balance})
            </option>
          ))}
        </select>

        <label htmlFor="destination-account">Cuenta destino</label>
        <select
          id="destination-account"
          name="destinationAccountId"
          required
          value={formState.destinationAccountId}
          onChange={(event) => handleChange("destinationAccountId", event.target.value)}
          disabled={isLoadingAccounts || isSubmitting}
        >
          <option value="">Selecciona una cuenta</option>
          {accounts.map((account) => (
            <option
              key={account.id}
              value={account.id}
              disabled={String(account.id) === formState.sourceAccountId}
            >
              {account.accountNumber} - {account.ownerName}
            </option>
          ))}
        </select>

        <label htmlFor="transfer-amount">Monto</label>
        <input
          id="transfer-amount"
          name="amount"
          type="number"
          min="0.01"
          step="0.01"
          required
          value={formState.amount}
          onChange={(event) => handleChange("amount", event.target.value)}
          placeholder="Ej. 25000.00"
        />

        <button type="submit" disabled={isSubmitting || isLoadingAccounts || accounts.length < 2}>
          {isSubmitting ? "Procesando..." : "Realizar transferencia"}
        </button>
      </form>

      {transaction?.qrCodeBase64 && (
        <div className="transfer-form__success" role="status">
          <h3>Transferencia realizada correctamente</h3>
          <p>Escanea el código QR para consultar los datos de la transacción.</p>
          {transaction.qrCodeBase64.startsWith("data:image/") ? (
            <img
              src={transaction.qrCodeBase64}
              width={220}
              height={220}
              alt="Código QR de la transferencia"
            />
          ) : (
            <QRCodeCanvas
              value={transaction.qrCodeBase64}
              size={220}
              includeMargin
              aria-label="Código QR de la transferencia"
            />
          )}
        </div>
      )}
    </section>
  );
}
