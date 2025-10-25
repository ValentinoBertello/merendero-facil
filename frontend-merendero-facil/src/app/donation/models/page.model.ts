export interface Page<T> {
    content: T[];
    size: number;
    totalElements: number;
    totalPages: number;
    number: number; // página actual (0-based)
    first: boolean;
    last: boolean;
    numberOfElements: number;
}