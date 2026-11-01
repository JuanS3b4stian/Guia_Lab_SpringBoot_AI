import axios from "axios";
import type {
  AccountResponse,
  TransactionRequest,
  TransactionResponse,
} from "../types/transaction";

const transactionApi = axios.create({
  baseURL: import.meta.env.VITE_BACKEND_URL ?? "http://localhost:8080",
  auth: {
    username: import.meta.env.VITE_API_USER ?? "user",
    password: import.meta.env.VITE_API_PASSWORD ?? "",
  },
  headers: {
    "Content-Type": "application/json",
  },
});

/**
 * Provides frontend operations for money transfer transactions.
 */
class TransactionService {
  /**
   * Retrieves the complete transaction history.
   *
   * @returns a promise containing all transactions
   */
  async getAllTransactions(): Promise<TransactionResponse[]> {
    const response = await transactionApi.get<TransactionResponse[]>("/transactions");
    return response.data;
  }

  /**
   * Retrieves all accounts available for transfers.
   *
   * @returns a promise containing all registered accounts
   */
  async getAllAccounts(): Promise<AccountResponse[]> {
    const response = await transactionApi.get<AccountResponse[]>("/accounts");
    return response.data;
  }

  /**
   * Creates a new money transfer.
   *
   * @param request source account, destination account and amount
   * @returns a promise containing the created transaction and its QR code
   */
  async createTransaction(
    request: TransactionRequest,
  ): Promise<TransactionResponse> {
    const response = await transactionApi.post<TransactionResponse>(
      "/transactions",
      request,
    );
    return response.data;
  }

  /**
   * Reverts a transaction by its identifier.
   *
   * @param transactionId identifier of the transaction to revert
   * @returns a promise resolved when the transaction has been reverted
   */
  async revertTransaction(transactionId: number): Promise<void> {
    await transactionApi.delete(`/transactions/${transactionId}`);
  }
}

export default new TransactionService();
