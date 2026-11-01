export interface TransactionRequest {
  sourceAccountId: number;
  destinationAccountId: number;
  amount: number;
}

export type TransactionStatus = "COMPLETED" | "FAILED" | "PENDING";

export interface TransactionResponse {
  id: number;
  sourceAccountId: number;
  destinationAccountId: number;
  amount: number;
  transactionDate: string;
  status: TransactionStatus;
  qrCodeBase64: string | null;
}

export interface AccountResponse {
  id: number;
  accountNumber: string;
  ownerName: string;
  balance: number;
}
