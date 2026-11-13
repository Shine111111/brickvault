export enum RetirementStatus {
  ACTIVE = 'ACTIVE',
  RETIRING_SOON = 'RETIRING_SOON',
  RETIRED = 'RETIRED',
}

export enum ItemCondition {
  SEALED_NISB = 'SEALED_NISB',
  BUILT_DISPLAY = 'BUILT_DISPLAY',
  LOOSE_COMPLETE = 'LOOSE_COMPLETE',
}

export type AccountType = 'PLAYER' | 'GAME_OWNER';

export interface AccountDto {
  id: number;
  name: string;
  email: string;
  accountType?: AccountType;
  createdAt?: string;
}

export interface UserSummaryDto {
  id: number;
  name: string;
  email: string;
  gameOwner?: boolean;
}

export interface LegoSetSummaryDto {
  id: number;
  setNumber: string;
  name: string;
  theme: string;
  pieceCount: number;
  releaseYear: number;
  retailPriceCad: number;
  estimatedResaleValueCad: number;
  retirementStatus: RetirementStatus;
  imageUrl?: string | null;
}

export interface LegoSetDto extends LegoSetSummaryDto {
  description?: string | null;
  averageRating: number;
  reviewCount: number;
}

export type LegoSetResponseDto = LegoSetDto;

export interface CollectionItemDto {
  id: number;
  legoSet?: LegoSetSummaryDto | null;
  owner?: UserSummaryDto | null;
  condition: ItemCondition;
  purchasePriceCad?: number | null;
  purchaseDate?: string | null;
  forTradeOrSale: boolean;
  available: boolean;
  notes?: string | null;
}

export type CollectionItemResponseDto = CollectionItemDto;

export enum TradeRequestStatus {
  PENDING = 'PENDING',
  APPROVED = 'APPROVED',
  DECLINED = 'DECLINED',
  CANCELLED = 'CANCELLED',
  RETURNED = 'RETURNED',
}

export interface TradeRequestDto {
  id: number;
  requesterId: number;
  requestedSetId: number;
  collectionItemId: number;
  startDate: string;
  endDate: string;
  status: TradeRequestStatus;
  requestDate: string;
}

export type LendingRecordStatus = 'ACTIVE' | 'RETURNED' | 'OVERDUE' | 'CLOSED';

export interface LendingRecordSetInfo {
  id: number;
  setNumber: string;
  name: string;
  theme: string;
  imageUrl?: string | null;
}

export interface LendingRecordDto {
  id: number;
  startDate: string;
  endDate: string;
  status: LendingRecordStatus;
  legoSet?: LendingRecordSetInfo | null;
  borrower?: UserSummaryDto | null;
  owner?: UserSummaryDto | null;
  durationInDays: number;
  isDamaged: boolean;
  damageNotes?: string | null;
  damageSeverity: number;
  damageAssessmentDate?: string | null;
}

export interface EventSetDto {
  id: number;
  setNumber: string;
  name: string;
  imageUrl?: string | null;
}

export interface EventResponse {
  eventId: string;
  title: string;
  dateTime: string;
  location: string;
  description: string;
  currentNumberParticipants: number;
  maxParticipants: number;
  featuredSet?: EventSetDto | null;
  host?: UserSummaryDto | null;
}

export interface RegistrationResponseDto {
  id: number;
  registrationDate: string;
  attendeeId: number;
  attendeeName: string;
  eventId: string;
}

export interface ReviewResponseDto {
  id: number;
  rating: number;
  comment: string;
  dateSubmitted: string;
  legoSetId?: number | null;
  setNumber?: string | null;
  setName?: string | null;
  reviewer?: AccountDto | null;
}
