export type AccountType = 'PLAYER' | 'GAME_OWNER';

export interface AccountDto {
  id: number;
  name: string;
  email: string;
  accountType: AccountType;
  createdAt?: string;
}

export interface UserSummaryDto {
  id: number;
  name: string;
  email: string;
}

export interface GameSummaryDto {
  id: number;
  name: string;
  minPlayers: number;
  maxPlayers: number;
  image?: string;
  category?: string;
}

export interface GameResponseDto extends GameSummaryDto {
  owner?: UserSummaryDto;
  averageRating?: number;
  reviewCount?: number;
}

export interface GameInstanceResponseDto {
  id: number;
  gameId: number;
  gameName?: string;
  condition: string;
  location?: string;
  available: boolean;
  ownerEmail?: string;
}

export type BorrowRequestStatus =
  | 'PENDING'
  | 'APPROVED'
  | 'DECLINED'
  | 'CANCELLED'
  | 'RETURNED';

export interface BorrowRequestDto {
  id: number;
  requesterId: number;
  requestedGameId: number;
  gameInstanceId?: number;
  startDate: string;
  endDate: string;
  status: BorrowRequestStatus;
  createdAt?: string;
}

export type LendingRecordStatus = 'ACTIVE' | 'RETURNED' | 'OVERDUE' | 'CLOSED';

export interface LendingRecordDto {
  id: number;
  borrowRequestId?: number;
  startDate: string;
  endDate: string;
  status: LendingRecordStatus;
  recordOwner?: UserSummaryDto;
  borrower?: UserSummaryDto;
  game?: GameSummaryDto;
}

export interface EventResponse {
  id: number;
  title: string;
  dateTime: string;
  location: string;
  description: string;
  maxParticipants: number;
  host?: UserSummaryDto;
  featuredGame?: GameSummaryDto;
}

export interface RegistrationResponseDto {
  id: number;
  eventId: number;
  attendeeEmail: string;
  registrationDate: string;
}

export interface ReviewResponseDto {
  id: number;
  rating: number;
  comment: string;
  dateSubmitted: string;
  author?: UserSummaryDto;
  gameId?: number;
}
