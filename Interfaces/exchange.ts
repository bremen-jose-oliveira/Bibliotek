import type Book from "@/Interfaces/book";
import type { UserSummary } from "@/Interfaces/user";

export enum ExchangeStatus {
  REQUESTED = "REQUESTED",
  APPROVED = "APPROVED",
  REJECTED = "REJECTED",
  COMPLETED = "COMPLETED",
}

export interface Exchange {
  id: number;
  book?: Book;
  borrower?: UserSummary;
  status: ExchangeStatus;
  exchangeDate?: string;
  createdAt?: string;
  updatedAt?: string;
}




