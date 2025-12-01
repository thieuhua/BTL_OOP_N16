# HƯỚNG DẪN MODIFY CODE - CHESS GAME PROJECT

**Project**: Chess Game OOP - Nhóm 16  
**Ngày tạo**: 29/11/2025  
**Mục đích**: Hướng dẫn modify code nhanh trong buổi báo cáo/chấm điểm

---

## MỤC LỤC
1. [Modify Nước Đi Của Quân Cờ](#1-modify-nước-đi-của-quân-cờ)
2. [Modify Timer (Đồng Hồ Đếm Giờ)](#2-modify-timer-đồng-hồ-đếm-giờ)
3. [Save/Load Game (Lưu Và Tải Game)](#3-saveload-game-lưu-và-tải-game)
4. [Hiểu Về Cấu Trúc Dữ Liệu Cơ Bản (ChessPosition & ChessMove)](#3b-hiểu-về-cấu-trúc-dữ-liệu-cơ-bản-chessposition--chessmove)
5. [Modify Các Tham Số Game (GameConstants)](#4-modify-các-tham-số-game-gameconstants)
6. [Modify Các Luật Cờ Vua (BoardState)](#4b-modify-các-luật-cờ-vua-boardstate)
7. [Modify Logic Kiểm Tra Nước Đi (ChessPiece)](#5-modify-logic-kiểm-tra-nước-đi-chesspiece)
8. [Modify Toolbar (Các Nút Chức Năng)](#6-modify-toolbar-các-nút-chức-năng)
9. [Modify Âm Thanh](#7-modify-âm-thanh)
10. [Modify Giá Trị Quân Cờ](#8-modify-giá-trị-quân-cờ)
11. [Hiểu Về Logic Chiếu Tướng Và Ăn Quân](#9-hiểu-về-logic-chiếu-tướng-và-ăn-quân)
12. [Thông Tin Bổ Sung Về Cấu Trúc UI](#10-thông-tin-bổ-sung-về-cấu-trúc-ui)
13. [Những Điều Cần Biết Khi Báo Cáo](#11-những-điều-cần-biết-khi-báo-cáo)

---

## 1. MODIFY NƯỚC ĐI CỦA QUÂN CỜ

### Mức độ: TRUNG BÌNH - DỄ
**Vị trí**: `src/main/java/nhom16oop/core/pieces/`

### Cách thức hoạt động:
- Mỗi loại quân cờ có 1 file riêng (King.java, Queen.java, Rook.java, Bishop.java, Knight.java, Pawn.java)
- Hàm chính: `generateValidMoves()` - tạo danh sách các nước đi hợp lệ
- Modify ở đây **KHÔNG GÂY ẢNH HƯỞNG** đến cấu trúc tổng thể, chỉ ảnh hưởng đến logic của quân đó

### VD 1: CHO VUA ĐI NHƯ XE (Rook)

**File**: `src/main/java/nhom16oop/core/pieces/King.java`

**Bước 1**: Tìm hàm `generateValidMoves()` (dòng ~22)

**Bước 2**: COMMENT CODE CŨ (8 hướng đi 1 ô):
```java
// ORIGINAL: King moves 1 square in 8 directions
// int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
// for (int[] dir : directions) {
//     int newCol = startCol + dir[0];
//     int newRow = startRow + dir[1];
//     if (BoardUtils.isWithinBoard(newCol, newRow)) {
//         ChessPosition pos = new ChessPosition(newCol, newRow);
//         if (!pieceMap.hasPiece(pos) || (pieceMap.hasPiece(pos) && pieceMap.getPiece(pos).getColor() != color)) {
//             moves.add(new ChessMove(start, pos));
//         }
//     }
// }
```

**Bước 3**: THÊM CODE MỚI (sao chép từ Rook.java dòng 28-48):
```java
// MODIFIED: King moves like Rook (straight lines unlimited distance)
int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};  // 4 hướng: lên, xuống, trái, phải
for (int[] dir : directions) {
    int dCol = dir[0];
    int dRow = dir[1];
    int newCol = startCol + dCol;
    int newRow = startRow + dRow;

    while (BoardUtils.isWithinBoard(newCol, newRow)) {
        ChessPosition endPos = new ChessPosition(newCol, newRow);
        if (!pieceMap.hasPiece(endPos)) {
            moves.add(new ChessMove(start, endPos));
        } else {
            if (pieceMap.getPiece(endPos).getColor() != getColor()) {
                moves.add(new ChessMove(start, endPos));
            }
            break;
        }
        newCol += dCol;
        newRow += dRow;
    }
}
```

**Bước 4**: Nếu cần tắt cả chức năng nhập thành, COMMENT dòng 40-48:
```java
// if (canCastleKingside(start, pieceMap)) {
//     moves.add(new ChessMove(start, new ChessPosition(6, start.row())));
// }
// 
// if (canCastleQueenside(start, pieceMap)) {
//     moves.add(new ChessMove(start, new ChessPosition(2, start.row())));
// }
```

### VD 2: CHO VUA ĐI NHƯ TƯỢNG (Bishop)

Tương tự VD1, nhưng sao chép code từ `Bishop.java` (dòng 28-48):
```java
// MODIFIED: King moves like Bishop (diagonal lines unlimited distance)
int[][] directions = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};  // 4 đường chéo
// ... (code tương tự Rook nhưng dùng 4 hướng chéo)
```

### VD 3: CHO VUA ĐI NHƯ HẬU (Queen)

Sao chép code từ `Queen.java` (dòng 22-54):
```java
// MODIFIED: King moves like Queen (8 directions unlimited distance)
int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
// ... (code tương tự Rook nhưng dùng 8 hướng)
```

### VD 4: GIỚI HẠN SỐ BƯỚC ĐI

Thêm biến giới hạn:
```java
int MAX_DISTANCE = 3;  // Chỉ đi tối đa 3 ô

while (BoardUtils.isWithinBoard(newCol, newRow)) {
    int distance = Math.abs(newCol - startCol) + Math.abs(newRow - startRow);
    if (distance > MAX_DISTANCE) break;  // THÊM DÒNG NÀY
    
    // ... code còn lại
}
```

### LƯU Ý QUAN TRỌNG:
- Sau khi modify, code vẫn hoạt động bình thường vì:
  - Hàm `isValidMove()` vẫn tự động kiểm tra chiếu tướng
  - `BoardUtils.isKingInCheck()` vẫn hoạt động
- **KHÔNG CẦN** sửa file nào khác
- **KHÔNG ẢNH HƯỞNG** đến cấu trúc OOP

---

## 2. MODIFY TIMER (ĐỒNG HỒ ĐẾM GIỜ)

### Mức độ: DỄ
**File chính**: 
- `src/main/java/nhom16oop/game/ChessTimer.java` (Logic timer)
- `src/main/java/nhom16oop/game/ChessController.java` (Khởi tạo timer)
- `src/main/java/nhom16oop/ui/ChessUI.java` (Ẩn/hiện UI)

### A. ẨN/HIỆN TIMER CHO TỪNG MODE

**Cách 1: Ẩn hoàn toàn UI timer (không khởi tạo, không chạy)**

**File**: `src/main/java/nhom16oop/ui/ChessUI.java`

Tìm constructor tương ứng với mode muốn ẩn timer:

**Ẩn timer cho Puzzle Mode** (dòng ~85-95):
```java
public ChessUI(int gameMode, PieceColor selectedColor, String puzzleFEN, int maxMoves) {
    // ...existing code...
    
    setupUI();

    // Hide timer UI after setupUI for puzzle mode
    if (gameMode == GameMode.PUZZLE_MODE) {
        chessController.hideTimerUI();  // ← DÒNG NÀY ĐÃ CÓ SẴN
    }
}
```

**Ẩn timer cho Player vs AI** (dòng ~35-42):
```java
public ChessUI(int gameMode, PieceColor selectedColor) {
    // ...existing code...
    
    setupUI();

    // Hide timer UI after setupUI for Player vs AI mode
    if (gameMode == GameMode.PLAYER_VS_AI) {
        chessController.hideTimerUI();  // ← DÒNG NÀY ĐÃ CÓ SẴN
    }
}
```

**Cách 2: Hiện lại timer (bỏ comment hoặc xóa dòng hideTimerUI)**

```java
// COMMENT hoặc XÓA dòng này để hiện timer
// if (gameMode == GameMode.PUZZLE_MODE) {
//     chessController.hideTimerUI();
// }
```

**Cách 3: Ẩn timer cho mode khác**

Thêm điều kiện tương tự:
```java
setupUI();

// Ẩn timer cho Player vs Player (nếu muốn)
if (gameMode == GameMode.PLAYER_VS_PLAYER) {
    chessController.hideTimerUI();
}
```

### B. THAY ĐỔI THỜI GIAN TIMER

**File**: `src/main/java/nhom16oop/game/ChessController.java`

Tìm các hàm setup mode:

**Thay đổi thời gian Player vs Player** (dòng ~104):
```java
public void setPlayerVsPlayer() {
    // ...existing code...
    
    // Initialize timer for Player vs Player mode (5 minutes per player)
    initializeTimer(5);  // ← SỬA SỐ NÀY
    
    // MODIFY:
    // initializeTimer(3);   // 3 phút
    // initializeTimer(10);  // 10 phút
    // initializeTimer(1);   // 1 phút (test nhanh)
}
```

**Thay đổi thời gian Player vs AI** (tìm hàm `setPlayerVsAI()`):
```java
public void setPlayerVsAI(PieceColor playerColor) {
    // ...existing code...
    
    // Nếu có initializeTimer(), sửa số phút ở đây
    initializeTimer(5);  // ← SỬA SỐ NÀY
}
```

### C. THAY ĐỔI TỐC ĐỘ ĐẾM

**File**: `src/main/java/nhom16oop/game/ChessTimer.java`

Tìm hàm `initializeTimers()` (dòng ~76):

```java
private void initializeTimers() {
    whiteTimer = new Timer(1000, e -> {  // ← SỬA SỐ NÀY
        // ...existing code...
    });
    
    blackTimer = new Timer(1000, e -> {  // ← SỬA SỐ NÀY
        // ...existing code...
    });
}
```

**MODIFY tốc độ**:
```java
// Mặc định: 1000ms = 1 giây
new Timer(1000, e -> { ... })

// Đếm nhanh hơn (test):
new Timer(100, e -> { ... })   // Đếm mỗi 0.1 giây

// Đếm chậm hơn:
new Timer(2000, e -> { ... })  // Đếm mỗi 2 giây
```

---

## 3. SAVE/LOAD GAME (LƯU VÀ TẢI GAME)

### Mức độ: DỄ - CHỈ ĐỌC ĐỂ HIỂU
**File chính**: 
- `src/main/java/nhom16oop/history/GameSave.java` (Định nghĩa cấu trúc lưu game)
- `src/main/java/nhom16oop/history/FileManager.java` (Xử lý lưu/đọc file)
- `src/main/java/nhom16oop/game/ChessController.java` (Hàm save/load)

### CÁCH HOẠT ĐỘNG:

**1. Lưu game**:
- Khi click nút "Back" trên Toolbar hoặc đóng cửa sổ, có tùy chọn Save
- Game được lưu dưới dạng JSON trong thư mục `saves/`
- File name: `save-{gameMode}-{timestamp}.json`

**2. Thông tin được lưu**:
- FEN string (trạng thái bàn cờ)
- Game mode (Player vs Player, Player vs AI, Puzzle)
- Màu quân được chọn
- Lịch sử nước đi
- Thời gian còn lại của timer

**3. Load game**:
- Trong ChessLauncher, chọn "Continue" để load game gần nhất
- Constructor `ChessUI(GameSave gameSave)` khôi phục trạng thái

### VỊ TRÍ FILE SAVE:
```
BTL_OOP_N16/
└── saves/
    ├── save-1-1764410455406.json  (Player vs Player)
    ├── save-2-1764410579876.json  (Player vs AI)
    └── save-3-1764410597395.json  (Puzzle Mode)
```

### LƯU Ý:
- **KHÔNG CẦN** sửa phần này trong buổi báo cáo
- Chỉ cần HIỂU cách hoạt động để giải thích
- File save có thể mở bằng text editor để xem cấu trúc JSON

---

## 3B. HIỂU VỀ CẤU TRÚC DỮ LIỆU CƠ BẢN (CHESSPOSITION & CHESSMOVE)

### Mức độ: CHỈ ĐỌC ĐỂ HIỂU - TUYỆT ĐỐI KHÔNG NÊN SỬA

**File**: 
- `src/main/java/nhom16oop/core/model/ChessPosition.java` (Vị trí trên bàn cờ)
- `src/main/java/nhom16oop/core/model/ChessMove.java` (Nước đi)

### A. CHESSPOSITION - ĐẠI DIỆN CHO 1 Ô TRÊN BÀN CỜ

#### **Định nghĩa:**
```java
public record ChessPosition(int col, int row)
// col: 0-7 (a-h), row: 0-7 (rank 1-8)
```

#### **Chức năng chính:**

**1. Chuyển đổi giữa ký hiệu cờ vua và tọa độ:**
```java
ChessPosition pos = ChessPosition.get("e4");       // "e4" → ChessPosition(4, 3)
String notation = pos.toChessNotation();           // ChessPosition(4, 3) → "e4"
```

**2. Validation tự động:**
- Chỉ chấp nhận vị trí trong bàn cờ 8x8
- Throw `InvalidPositionException` nếu vị trí không hợp lệ

**3. Cache Pattern:**
- Tạo sẵn 64 object cho tất cả ô a1→h8
- Không tạo object mới mỗi lần gọi → tiết kiệm bộ nhớ

**4. Chuyển đổi hệ tọa độ Matrix (cho UI):**
```java
int matrixRow = position.matrixRow();  // Đảo ngược: Rank 8 → Row 0, Rank 1 → Row 7
```

#### **Được sử dụng ở đâu:**
- **ChessMove**: Lưu vị trí start và end
- **ChessPieceMap**: Key của Map<ChessPosition, ChessPiece>
- **Các quân cờ**: Tính toán vị trí mới khi di chuyển
- **UI**: Mỗi ô ChessTile có 1 ChessPosition

### B. CHESSMOVE - ĐẠI DIỆN CHO 1 NƯỚC ĐI

#### **Định nghĩa:**
```java
public record ChessMove(ChessPosition start, ChessPosition end)
```

#### **Chức năng chính:**

**1. Lưu trữ nước đi:**
- `start`: Vị trí ban đầu của quân cờ
- `end`: Vị trí đích sau khi di chuyển

**2. Chuyển đổi sang ký hiệu:**
```java
ChessMove move = new ChessMove(e2, e4);
String notation = move.moveNotation();  // "e2e4"
```

#### **Được sử dụng ở đâu:**
- **Các quân cờ**: `generateValidMoves()` trả về `List<ChessMove>`
- **ChessPiece**: `isValidMove(ChessMove move)` kiểm tra hợp lệ
- **BoardUtils**: Mô phỏng nước đi, kiểm tra chiếu vua
- **UI**: Highlight các ô có thể di chuyển
- **Stockfish AI**: Parse nước đi từ engine

### C. MỐI QUAN HỆ GIỮA CHESSPOSITION VÀ CHESSMOVE

```
ChessPosition (1 ô trên bàn cờ)
    ↓
    Được sử dụng bởi
    ↓
ChessMove (1 nước đi = 2 ChessPosition)
```

**So sánh:**

| **Đặc điểm** | **ChessPosition** | **ChessMove** |
|-------------|-------------------|---------------|
| **Đại diện** | 1 ô trên bàn cờ | 1 nước đi |
| **Dữ liệu** | `(col, row)` | `(start, end)` |
| **Ví dụ** | `e4` | `e2→e4` |
| **Số lượng** | 64 (a1→h8) | Vô hạn |

**Ví dụ cụ thể:**
```java
// TẠO VỊ TRÍ
ChessPosition e2 = ChessPosition.get("e2");  // Cột 4, hàng 1
ChessPosition e4 = ChessPosition.get("e4");  // Cột 4, hàng 3

// TẠO NƯỚC ĐI
ChessMove move = new ChessMove(e2, e4);      // Di chuyển từ e2 đến e4

// Quân cờ tạo danh sách nước đi
List<ChessMove> moves = piece.generateValidMoves(e2, pieceMap);
// → [e2→e3, e2→e4, e2→e5, ...]

// Kiểm tra nước đi hợp lệ
if (piece.isValidMove(move, pieceMap)) {
    // Thực hiện nước đi
}
```

### D. TẠI SAO KHÔNG NÊN MODIFY?

**❌ ChessPosition.java:**
- Core data structure được dùng **MỌI NƠI**
- Thay đổi sẽ **PHÁ VỠ TOÀN BỘ GAME**
- Validation và cache đã tối ưu
- Record class immutable - đảm bảo an toàn

**❌ ChessMove.java:**
- Lightweight data structure
- Được tạo và hủy liên tục trong game
- Thay đổi ảnh hưởng đến tất cả logic game
- Record class tối ưu, không cần thêm gì

### LƯU Ý KHI BÁO CÁO:

**Nếu thầy hỏi về 2 file này:**

✅ **Giải thích được:**
- ChessPosition = 1 ô, ChessMove = 1 nước đi
- ChessMove SỬ DỤNG 2 ChessPosition
- Record class = immutable, tự động có equals/hashCode
- ChessPosition có cache 64 object sẵn

✅ **Nhấn mạnh:**
- Đây là **core data structures**
- Thể hiện tư duy **abstraction** trong OOP
- Được sử dụng xuyên suốt project (Model, View, Controller)

✅ **Không modify vì:**
- Quá nguy hiểm, ảnh hưởng toàn bộ
- Đã tối ưu, không cần thêm
- Phá vỡ cấu trúc OOP

---

## 4. MODIFY CÁC THAM SỐ GAME (GAMECONSTANTS)

### Mức độ: DỄ
**File**: `src/main/java/nhom16oop/constants/GameConstants.java`

File này chứa các constant quan trọng của game có thể dễ dàng modify.

### A. THAY ĐỔI KÍCH THƯỚC Ô CỜ

```java
public static final class Board {
    public static final int BOARD_SIZE = 8;         // Số ô trên bàn cờ (8x8) - KHÔNG NÊN SỬA
    public static final int SQUARE_SIZE = 100;      // <-- SỬA DÒNG NÀY: Kích thước mỗi ô (pixel)
    public static final int BOARD_WIDTH = BOARD_SIZE * SQUARE_SIZE;   // Tự động tính
    public static final int BOARD_HEIGHT = BOARD_SIZE * SQUARE_SIZE;  // Tự động tính
}
```

**Ví dụ thay đổi kích thước ô cờ**:
```java
public static final int SQUARE_SIZE = 80;   // Bàn cờ nhỏ hơn (640x640)
public static final int SQUARE_SIZE = 120;  // Bàn cờ lớn hơn (960x960)
public static final int SQUARE_SIZE = 60;   // Bàn cờ rất nhỏ (test)
```

**Hiệu ứng**: 
- Thay đổi kích thước toàn bộ bàn cờ và quân cờ
- Hình ảnh tự động scale theo kích thước mới
- Cửa sổ game tự động điều chỉnh

### B. THAY ĐỔI LUẬT 50 NƯỚC ĐI (FIFTY MOVE RULE)

```java
public static final int FIFTY_MOVE_RULE_LIMIT = 50;  // <-- SỬA DÒNG NÀY
```

**Giải thích**: Trong cờ vua, nếu có 50 nước đi liên tiếp mà không có quân nào bị ăn hoặc tốt nào di chuyển, ván đấu sẽ tự động hòa.

**Ví dụ thay đổi giới hạn**:
```java
public static final int FIFTY_MOVE_RULE_LIMIT = 30;   // Hòa nhanh hơn (30 nước)
public static final int FIFTY_MOVE_RULE_LIMIT = 100;  // Chơi lâu hơn (100 nước)
public static final int FIFTY_MOVE_RULE_LIMIT = 10;   // Test nhanh
```

**Vị trí áp dụng**: 
- File: `src/main/java/nhom16oop/game/ChessController.java` (dòng ~699)
- Hàm: `checkForDraw()` - kiểm tra điều kiện hòa

**Cách test**:
1. Sửa `FIFTY_MOVE_RULE_LIMIT = 5` (ví dụ)
2. Rebuild project (Ctrl+F9)
3. Chơi game và di chuyển quân cờ không ăn quân
4. Sau 5 nước, game sẽ hiện thông báo hòa

### LƯU Ý QUAN TRỌNG:
- Sau khi sửa **BẤT KỲ CONSTANT NÀO** trong file này cần **Rebuild project** (Ctrl+F9)
- `BOARD_SIZE = 8` **KHÔNG NÊN SỬA** vì sẽ phá vỡ logic game (cờ vua luôn là 8x8)
- `BOARD_WIDTH` và `BOARD_HEIGHT` **TỰ ĐỘNG TÍNH** dựa vào SQUARE_SIZE, không cần sửa

---

## 4B. MODIFY CÁC LUẬT CỜ VUA (BOARDSTATE)

### Mức độ: TRUNG BÌNH
**File**: `src/main/java/nhom16oop/core/model/BoardState.java`

File này quản lý **trạng thái bàn cờ** và các **luật cờ vua đặc biệt** như nhập thành, en passant, luật 50 nước đi.

### NỘI DUNG FILE:
- Lưu trữ vị trí tất cả quân cờ trên bàn
- Quản lý lượt chơi hiện tại (WHITE/BLACK)
- Theo dõi quyền nhập thành (castling rights)
- Xử lý nước đi en passant (ăn tốt qua đường)
- Chuyển đổi FEN string (Forsyth-Edwards Notation)

### A. TẮT HOÀN TOÀN NHẬP THÀNH (CASTLING)

**Mức độ**: DỄ - CHỈ COMMENT 1 ĐOẠN CODE

**Vị trí**: Hàm `setLastMove()` (dòng ~129-158)

**Cách làm**: Comment toàn bộ phần xử lý castling rights

```java
public void setLastMove(ChessMove lastMove) {
    this.lastMove = lastMove;
    updateEnPassantTargetSquare();

    // COMMENT TOÀN BỘ ĐOẠN NÀY ĐỂ TẮT CASTLING
    // if (lastMove != null) {
    //     ChessPiece movedPiece = chessPieceMap.getPiece(lastMove.start());
    //     ChessPosition start = lastMove.start();
    //     if (movedPiece instanceof King) {
    //         if (movedPiece.getColor() == PieceColor.WHITE) {
    //             whiteCanCastleKingside = false;
    //             whiteCanCastleQueenside = false;
    //         } else {
    //             blackCanCastleKingside = false;
    //             blackCanCastleQueenside = false;
    //         }
    //     } else if (movedPiece instanceof Rook) {
    //         if (movedPiece.getColor() == PieceColor.WHITE) {
    //             if (start.equals(ChessPosition.get("H1"))) {
    //                 whiteCanCastleKingside = false;
    //             } else if (start.equals(ChessPosition.get("A1"))) {
    //                 whiteCanCastleQueenside = false;
    //             }
    //         } else {
    //             if (start.equals(ChessPosition.get("H8"))) {
    //                 blackCanCastleKingside = false;
    //             } else if (start.equals(ChessPosition.get("A8"))) {
    //                 blackCanCastleQueenside = false;
    //             }
    //         }
    //     }
    // }
}
```

**Kết quả**: Game sẽ KHÔNG CHO PHÉP nhập thành trong suốt ván đấu.

### B. CHỈ TẮT NHẬP THÀNH PHÍ A VUA (KINGSIDE)

**Vị trí**: Trong hàm `setLastMove()`, comment một phần

```java
if (movedPiece instanceof King) {
    if (movedPiece.getColor() == PieceColor.WHITE) {
        whiteCanCastleKingside = false;  // Giữ dòng này
        // whiteCanCastleQueenside = false;  // Comment dòng này
    } else {
        blackCanCastleKingside = false;  // Giữ dòng này
        // blackCanCastleQueenside = false;  // Comment dòng này
    }
}
```

**Kết quả**: Chỉ cho phép nhập thành phía hậu (queenside), không cho phép nhập thành phía vua (kingside).

### C. TẮT LUẬT EN PASSANT (ĂN TỐT QUA ĐƯỜNG)

**Mức độ**: DỄ

**Vị trí**: Hàm `updateEnPassantTargetSquare()` (dòng ~189-197)

```java
public void updateEnPassantTargetSquare() {
    // COMMENT TOÀN BỘ ĐỂ TẮT EN PASSANT
    // if (lastMove != null && chessPieceMap.getPiece(lastMove.end()) instanceof Pawn 
    //     && Math.abs(lastMove.start().row() - lastMove.end().row()) == 2) {
    //     int enPassantRow = (lastMove.start().row() + lastMove.end().row()) / 2;
    //     enPassantTargetSquare = new ChessPosition(lastMove.end().col(), enPassantRow);
    // } else {
    //     enPassantTargetSquare = null;
    // }
    
    // THÊM DÒNG NÀY
    enPassantTargetSquare = null;  // Luôn luôn null = không có en passant
}
```

**Kết quả**: Game sẽ KHÔNG CHO PHÉP ăn tốt qua đường.

### D. CHO PHÉP NHẬP THÀNH MỌI LÚC (BỎ QUA ĐIỀU KIỆN)

**Mức độ**: TRUNG BÌNH - CẦN THẬN TRỌNG

**Vị trí**: Constructor `BoardState(ChessPieceMap chessPieceMap)` (dòng ~45-69)

```java
public BoardState(ChessPieceMap chessPieceMap) {
    this.chessPieceMap = chessPieceMap;
    if (!chessPieceMap.getPieceMap().isEmpty()) {
        // COMMENT TOÀN BỘ PHẦN CHECK ĐIỀU KIỆN
        // ChessPosition whiteKingPosition = chessPieceMap.getKingPosition(PieceColor.WHITE);
        // if (whiteKingPosition == null) {
        //     throw new IllegalStateException("White King position is null");
        // }
        // King whiteKing = chessPieceMap.getKing(PieceColor.WHITE);
        // this.whiteCanCastleKingside = whiteKing.canCastleKingside(whiteKingPosition, chessPieceMap);
        // this.whiteCanCastleQueenside = whiteKing.canCastleQueenside(whiteKingPosition, chessPieceMap);
        
        // ... (tương tự cho Black King)
        
        // THÊM DÒNG NÀY - CHO PHÉP LUÔN
        this.whiteCanCastleKingside = true;
        this.whiteCanCastleQueenside = true;
        this.blackCanCastleKingside = true;
        this.blackCanCastleQueenside = true;
    }
}
```

**LƯU Ý**: Cách này có thể gây lỗi logic game vì cho phép nhập thành kể cả khi vua/xe đã di chuyển. **KHÔNG KHUYẾN KHÍCH**.

### E. THAY ĐỔI VỊ TRÍ NHẬP THÀNH

**Mức độ**: KHÓ - KHÔNG NÊN LÀM TRONG BÁO CÁO

Vị trí nhập thành được hard-code trong `King.java` (hàm `canCastleKingside()` và `canCastleQueenside()`). Thay đổi cần sửa nhiều file.

### F. HIỂU VỀ FEN STRING

**Vị trí**: Hàm `setFromFEN()` (dòng ~75-125)

FEN (Forsyth-Edwards Notation) là cách lưu trạng thái bàn cờ dưới dạng string.

**Ví dụ FEN**:
```
rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1
```

**Cấu trúc**:
1. **Vị trí quân cờ**: `rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR`
   - Chữ hoa = quân trắng, chữ thường = quân đen
   - Số = số ô trống
   - `/` = xuống hàng tiếp theo
   
2. **Lượt chơi**: `w` (white) hoặc `b` (black)

3. **Quyền nhập thành**: `KQkq`
   - K = White kingside, Q = White queenside
   - k = Black kingside, q = Black queenside
   - `-` = không ai có quyền nhập thành

4. **En passant target**: `-` (không có) hoặc vị trí (vd: `e3`)

5. **Halfmove clock**: Số nước đi không ăn quân/di chuyển tốt

6. **Fullmove number**: Số lượt đi đầy đủ

**Cách test**: Thay đổi FEN trong Puzzle mode để tạo vị trí khác nhau.

### LƯU Ý KHI MODIFY BOARDSTATE:

**✅ DỄ VÀ AN TOÀN**:
- Tắt nhập thành (comment code trong `setLastMove()`)
- Tắt en passant (comment code trong `updateEnPassantTargetSquare()`)

**⚠️ TRUNG BÌNH - CẦN THẬN TRỌNG**:
- Thay đổi điều kiện nhập thành trong constructor
- Sửa FEN parsing

**❌ KHÓ - KHÔNG NÊN LÀM**:
- Thay đổi vị trí nhập thành (cần sửa nhiều file)
- Thay đổi cấu trúc lưu trữ board state

**QUAN TRỌNG**: 
- File này là **CORE MODEL** của game
- Sửa sai có thể phá vỡ toàn bộ logic
- **LUÔN LUÔN REBUILD** sau khi sửa (Ctrl+F9)
- **TEST KỸ** trước khi báo cáo

---

## 5. MODIFY LOGIC KIỂM TRA NƯỚC ĐI (CHESSPIECE)

### Mức độ: TRUNG BÌNH - KHÓ
**File**: `src/main/java/nhom16oop/core/model/ChessPiece.java`

### GIỚI THIỆU VỀ FILE:

**ChessPiece.java** là **abstract base class** (lớp cha trừu tượng) cho TẤT CẢ quân cờ:
- King, Queen, Rook, Bishop, Knight, Pawn đều **KẾ THỪA** từ class này
- Chứa logic **KIỂM TRA NƯỚC ĐI HỢP LỆ** - ngăn người chơi đi nước khiến vua bị chiếu
- Là file **THỂ HIỆN TƯ DUY OOP** rõ nhất (Abstraction, Inheritance, Polymorphism)

### CÁC THUỘC TÍNH QUAN TRỌNG:

```java
private final PieceColor color;        // Màu quân cờ (WHITE/BLACK)
private Image image;                   // Hình ảnh quân cờ
protected int pieceValue = 0;          // Giá trị quân cờ
private boolean hasMoved = false;      // Đã di chuyển chưa
```

### A. TẮT KIỂM TRA CHIẾU VUA (CHO PHÉP ĐI NƯỚC ĐỂ VUA BỊ CHIẾU)

**Mức độ**: TRUNG BÌNH - DEMO TỐT CHO BÁO CÁO

**Vị trí**: Hàm `isValidMove()` (dòng ~93-105)

**Giải thích logic hiện tại**:
```java
public boolean isValidMove(ChessMove move, ChessPieceMap pieceMap) {
    // Bước 1: Lấy tất cả nước đi có thể của quân
    final List<ChessMove> moves = generateValidMoves(move.start(), pieceMap);

    // Bước 2: Với mỗi nước đi, kiểm tra xem có để vua bị chiếu không
    for (ChessMove chessMove : moves) {
        ChessPieceMap tempMap = BoardUtils.simulateMove(chessMove, pieceMap);
        
        // Kiểm tra: Nước đi có để vua bị chiếu không?
        if (!BoardUtils.isKingInCheck(this.color, tempMap) && chessMove.equals(move)) {
            return true;  // Chỉ chấp nhận nếu KHÔNG bị chiếu
        }
    }

    return false;
}
```

**MODIFY - TẮT KIỂM TRA CHIẾU VUA**:

```java
public boolean isValidMove(ChessMove move, ChessPieceMap pieceMap) {
    final List<ChessMove> moves = generateValidMoves(move.start(), pieceMap);

    // COMMENT TOÀN BỘ PHẦN KIỂM TRA CHIẾU VUA
    // for (ChessMove chessMove : moves) {
    //     ChessPieceMap tempMap = BoardUtils.simulateMove(chessMove, pieceMap);
    //     if (!BoardUtils.isKingInCheck(this.color, tempMap) && chessMove.equals(move)) {
    //         return true;
    //     }
    // }
    
    // THÊM CODE MỚI - CHỈ KIỂM TRA NƯỚC ĐI CÓ TRONG DANH SÁCH HAY KHÔNG
    for (ChessMove chessMove : moves) {
        if (chessMove.equals(move)) {
            return true;  // Chấp nhận bất kỳ nước đi nào trong danh sách
        }
    }

    return false;
}
```

**KẾT QUẢ**: 
- ✅ Người chơi có thể đi nước khiến vua bị chiếu
- ✅ Game KHÔNG tự động kiểm tra và ngăn chặn
- ⚠️ KHÔNG THEO LUẬT CỜ VUA CHUẨN (nhưng tốt cho demo)

**Cách test**:
1. Sửa code như trên
2. Rebuild project (Ctrl+F9)
3. Chơi game và cố tình đi nước để vua bị chiếu
4. Game sẽ CHO PHÉP thực hiện nước đi đó

### B. THAY ĐỔI KÍCH THƯỚC HÌNH ẢNH QUÂN CỜ MẶC ĐỊNH

**Mức độ**: DỄ

**Vị trí**: Constructor (dòng ~32-34)

```java
public ChessPiece(PieceColor color, String imageFileName) {
    this(color, imageFileName, 95);  // <-- SỬA SỐ NÀY
}
```

**Ví dụ**:
```java
this(color, imageFileName, 80);   // Quân cờ nhỏ hơn
this(color, imageFileName, 110);  // Quân cờ lớn hơn
```

**Lưu ý**: Nếu thay đổi `SQUARE_SIZE` trong `GameConstants.java` thì nên thay đổi size này tương ứng để quân cờ không bị quá to/nhỏ so với ô.

### C. THÊM LOGGING ĐỂ DEBUG

**Mức độ**: DỄ - TỐT CHO DEMO HIỂU CODE

**Vị trí**: Trong hàm `isValidMove()` (dòng ~93)

```java
public boolean isValidMove(ChessMove move, ChessPieceMap pieceMap) {
    // THÊM LOGGING
    logger.info("Checking move: " + move + " for piece: " + this.getPieceNotation());
    
    final List<ChessMove> moves = generateValidMoves(move.start(), pieceMap);
    
    // THÊM LOGGING
    logger.info("Generated " + moves.size() + " possible moves");

    for (ChessMove chessMove : moves) {
        ChessPieceMap tempMap = BoardUtils.simulateMove(chessMove, pieceMap);
        if (!BoardUtils.isKingInCheck(this.color, tempMap) && chessMove.equals(move)) {
            // THÊM LOGGING
            logger.info("Move is valid!");
            return true;
        }
    }
    
    // THÊM LOGGING
    logger.info("Move is invalid!");
    return false;
}
```

**KẾT QUẢ**: Trong console sẽ hiện log mỗi khi kiểm tra nước đi, giúp hiểu flow của code.

### D. HIỂU VỀ CÁC PHƯƠNG THỨC TRỪU TƯỢNG (ABSTRACT METHODS)

**KHÔNG MODIFY** các phương thức này trong ChessPiece.java, nhưng cần **HIỂU** để giải thích trong báo cáo:

#### **1. `generateValidMoves(ChessPosition start, ChessPieceMap pieceMap)`**

- **Chức năng**: Tạo danh sách tất cả nước đi có thể từ vị trí hiện tại
- **Abstract**: Mỗi quân cờ implement khác nhau
- **Ví dụ**:
  - `King.generateValidMoves()` → 8 hướng, mỗi hướng 1 ô + nhập thành
  - `Queen.generateValidMoves()` → 8 hướng không giới hạn
  - `Knight.generateValidMoves()` → 8 vị trí hình chữ L

#### **2. `getPieceNotation()`**

- **Chức năng**: Trả về ký hiệu quân cờ theo chuẩn quốc tế
- **Implement ở mỗi quân**:
  - King → "K"
  - Queen → "Q"
  - Rook → "R"
  - Bishop → "B"
  - Knight → "N"
  - Pawn → "P"

#### **3. `deepCopy()`**

- **Chức năng**: Tạo bản sao sâu của quân cờ
- **Dùng cho**: AI tính toán, mô phỏng nước đi, undo/redo

### E. HIỂU VỀ QUAN HỆ KẾ THỪA (INHERITANCE)

```
ChessPiece (Abstract) ← LỚP CHA
    ├── King.java           ← LỚP CON
    ├── Queen.java          ← LỚP CON
    ├── Rook.java           ← LỚP CON
    ├── Bishop.java         ← LỚP CON
    ├── Knight.java         ← LỚP CON
    └── Pawn.java           ← LỚP CON
```

**Các lớp con KẾ THỪA từ ChessPiece**:
- ✅ Thuộc tính: `color`, `image`, `pieceValue`, `hasMoved`
- ✅ Phương thức: `isValidMove()`, `getColor()`, `getImage()`, `compareTo()`
- ✅ PHẢI IMPLEMENT: `generateValidMoves()`, `getPieceNotation()`, `deepCopy()`

**Đây là ĐA HÌNH (Polymorphism)**:
```java
ChessPiece piece = new King(PieceColor.WHITE, "white_king.png");  // Biến kiểu cha
List<ChessMove> moves = piece.generateValidMoves(...);  // Gọi đúng King.generateValidMoves()
```

### LƯU Ý KHI MODIFY CHESSPIECE:

**✅ DỄ VÀ AN TOÀN**:
- Thêm logging để debug
- Thay đổi kích thước hình ảnh mặc định

**⚠️ TRUNG BÌNH - DEMO TỐT**:
- Tắt kiểm tra chiếu vua (comment code trong `isValidMove()`)
- Hiệu ứng rõ ràng, dễ test

**❌ KHÓ - KHÔNG NÊN LÀM**:
- Thay đổi cấu trúc thuộc tính (ảnh hưởng toàn bộ 6 lớp con)
- Xóa phương thức abstract (phá vỡ contract với lớp con)
- Thay đổi logic trong `compareTo()` (ảnh hưởng sắp xếp)

**QUAN TRỌNG**: 
- File này là **CORE OOP** của project - thể hiện Abstraction, Inheritance, Polymorphism
- Khi báo cáo, đây là file **PHẢI GIẢI THÍCH** về tư duy OOP
- **LUÔN LUÔN REBUILD** sau khi sửa (Ctrl+F9)
- **TEST KỸ** vì ảnh hưởng đến tất cả quân cờ

---

## 6. MODIFY TOOLBAR (CÁC NÚT CHỨC NĂNG)

### Mức độ: DỄ
**File**: `src/main/java/nhom16oop/ui/components/panels/ChessToolbar.java`

### CÁC NÚT HIỆN CÓ:
1. **Back** (Quay lại launcher) - dòng ~66
2. **Flip Board** (Đổi hướng bàn cờ) - dòng ~124
3. **Resign** (Đầu hàng) - dòng ~126
4. **Show Hint** (Gợi ý nước đi) - dòng ~133
5. **Move Back** (Undo) - dòng ~135
6. **Move Forward** (Redo) - dòng ~137

### ẨN/HIỆN TỪNG NÚT

**Tìm hàm `initializeButtonConfigs()` (dòng ~63)**

**Cách 1: Comment toàn bộ ButtonConfig để ẩn nút**

Ví dụ ẩn nút Resign:
```java
// buttonConfigs.add(new ButtonConfig("Resign", "images/resign.png", e -> {
//     ResignDialog dialog = new ResignDialog((Frame) SwingUtilities.getWindowAncestor(this), "Are you sure you want to resign?");
//     dialog.setVisible(true);
//     if (dialog.isConfirmed()) {
//         actionManager.resignGame();
//     }
// }, () -> !chessController.isGameEnded()));
```

Ví dụ ẩn nút Hint:
```java
// buttonConfigs.add(new ButtonConfig("Show hint", "images/hint.png", e -> actionManager.showHint(), () -> chessController.getGameMode() == GameMode.PLAYER_VS_AI && !chessController.isGameEnded()));
```

**Cách 2: Thay đổi điều kiện hiển thị (tham số cuối cùng)**

Ví dụ chỉ hiện Hint ở mode Puzzle:
```java
// ORIGINAL:
buttonConfigs.add(new ButtonConfig("Show hint", "images/hint.png", e -> actionManager.showHint(), 
    () -> chessController.getGameMode() == GameMode.PLAYER_VS_AI && !chessController.isGameEnded()));

// MODIFIED: Hiện ở cả Puzzle mode
buttonConfigs.add(new ButtonConfig("Show hint", "images/hint.png", e -> actionManager.showHint(), 
    () -> (chessController.getGameMode() == GameMode.PLAYER_VS_AI || chessController.getGameMode() == GameMode.PUZZLE_MODE) && !chessController.isGameEnded()));
```

Ví dụ ẩn nút Resign ở mode Puzzle:
```java
// ORIGINAL:
buttonConfigs.add(new ButtonConfig("Resign", "images/resign.png", e -> { ... }, 
    () -> !chessController.isGameEnded()));

// MODIFIED: Ẩn ở Puzzle mode
buttonConfigs.add(new ButtonConfig("Resign", "images/resign.png", e -> { ... }, 
    () -> chessController.getGameMode() != GameMode.PUZZLE_MODE && !chessController.isGameEnded()));
```

### LƯU Ý:
- Toolbar này là **CHUNG** cho tất cả các mode
- Mỗi nút có điều kiện hiển thị riêng (tham số `isVisible`)
- Để ẩn/hiện nút theo mode, sửa điều kiện trong tham số cuối cùng của `ButtonConfig`

---

## 7. MODIFY ÂM THANH

### Mức độ: RẤT DỄ
**File**: `src/main/java/nhom16oop/utils/SoundPlayer.java`

### TẮT TOÀN BỘ ÂM THANH

**Cách 1**: Comment tất cả hàm playSound():
```java
public static void playMoveSound() {
    // playSound("sounds/move-self.mp3");
}

public static void playCaptureSound() {
    // playSound("sounds/capture.mp3");
}
```

**Cách 2**: Thêm biến kiểm soát:
```java
private static final boolean SOUND_ENABLED = false;  // Sửa thành false

private static void playSound(String soundFile) {
    if (!SOUND_ENABLED) return;  // THÊM DÒNG NÀY
    // ... code còn lại
}
```

---

## 8. MODIFY GIÁ TRỊ QUÂN CỜ

### Mức độ: DỄ
**Vị trí**: Trong constructor mỗi quân cờ

### Giá trị mặc định:
- Pawn (Tốt): 1
- Knight (Mã): 3  
- Bishop (Tượng): 3
- Rook (Xe): 5
- Queen (Hậu): 9
- King (Vua): Integer.MAX_VALUE

### Cách sửa:

**File**: `src/main/java/nhom16oop/core/pieces/Queen.java`
```java
public class Queen extends ChessPiece {
    {
        this.pieceValue = 9;  // <-- SỬA DÒNG NÀY
    }
    // ...
}
```

**Ví dụ**: Tăng giá trị Hậu lên 15:
```java
this.pieceValue = 15;
```

---

## 9. HIỂU VỀ LOGIC CHIẾU TƯỚNG VÀ ĂN QUÂN

### Mức độ: KHUYẾN CÁO - CHỈ ĐỌC ĐỂ HIỂU, KHÔNG NÊN SỬA

### A. LOGIC CHIẾU TƯỚNG (CHECK)

**File chính**: `src/main/java/nhom16oop/utils/BoardUtils.java`

**Hàm quan trọng**:
- `isKingInCheck()` (dòng ~26): Kiểm tra vua có bị chiếu không
- `isCheckmate()` (dòng ~58): Kiểm tra chiếu hết
- `isMoveValidUnderCheck()` (dòng ~79): Kiểm tra nước đi có hợp lệ khi bị chiếu

**Cách hoạt động**:
1. Sau mỗi nước đi, `ChessController` gọi `isKingInCheck()` để kiểm tra
2. Mỗi quân cờ khi di chuyển, hàm `isValidMove()` tự động kiểm tra xem nước đi có để vua bị chiếu không
3. Nếu đang bị chiếu, bạn **CÓ THỂ**:
   - Di chuyển vua ra khỏi vị trí nguy hiểm
   - Chặn bằng quân khác vào giữa
   - Ăn quân địch đang chiếu vua

**LƯU Ý**: Logic này là **LUẬT CỜ VUA CHUẨN QUỐC TẾ**, không nên sửa!

### B. LOGIC ĂN QUÂN (CAPTURE)

**Vị trí**: Trong hàm `generateValidMoves()` của từng loại quân cờ

**Cách kiểm tra ăn quân**:
```java
// Kiểm tra ô đích có quân đối phương không
if (pieceMap.hasPiece(targetPos) && 
    pieceMap.getPiece(targetPos).getColor() != getColor()) {
    // Có thể ăn quân này
    moves.add(new ChessMove(start, targetPos));
}
```

**Các file liên quan**:
- `Pawn.java` (dòng ~53): Ăn chéo + En Passant
- `Knight.java` (dòng ~32): Ăn quân ở vị trí nhảy
- `Bishop.java`, `Rook.java`, `Queen.java`: Ăn quân trên đường đi
- `King.java` (dòng ~34): Ăn quân trong phạm vi 1 ô

**Xử lý sau khi ăn quân**:
- `ChessController.java`: Phát âm thanh, cập nhật UI
- `PlayerPanel.java`: Hiển thị quân bị ăn

### C. CÁC HÀM QUAN TRỌNG KHÔNG NÊN SỬA

```
src/main/java/nhom16oop/utils/BoardUtils.java
├── isKingInCheck()          // Kiểm tra chiếu tướng
├── isCheckmate()            // Kiểm tra chiếu hết
├── isStalemate()            // Kiểm tra hòa (bí)
├── isMoveValidUnderCheck()  // Kiểm tra nước đi khi bị chiếu
└── simulateMove()           // Mô phỏng nước đi để test

src/main/java/nhom16oop/core/model/ChessPiece.java
└── isValidMove()            // Kiểm tra nước đi hợp lệ (có check chiếu tướng)
```

---

## 10. THÔNG TIN BỔ SUNG VỀ CẤU TRÚC UI

### A. KÍCH THƯỚC WINDOW

**File chính**: `src/main/java/nhom16oop/ui/ChessUI.java`

Các thành phần ảnh hưởng đến kích thước:
- `GameConstants.Board.BOARD_WIDTH` và `BOARD_HEIGHT` (bàn cờ chính)
- `PlayerPanel` (hiển thị thông tin người chơi - 2 panel trên dưới)
- `ChessToolbar` (thanh công cụ)

**Modify kích thước tổng thể**:
Thay đổi `SQUARE_SIZE` trong `GameConstants.java` sẽ tự động scale toàn bộ UI.

### B. HINT (GỢI Ý NƯỚC ĐI)

**File**: `src/main/java/nhom16oop/engine/Stockfish.java`

Hint sử dụng **Stockfish engine** để tính toán nước đi tốt nhất:
- KHÔNG phải thuật toán tự viết
- Sử dụng file `stockfish.exe` (Windows) hoặc `stockfish` (Linux/Mac)
- Giao tiếp qua UCI protocol

**Highlight ô gợi ý**: 
- `ChessBoardUI.java` (dòng ~130): Hàm `generateAndHighlightValidMoves()`
- `ChessTile.java`: Vẽ các ô highlight khi click vào quân cờ

### C. CÁC LOẠI HIGHLIGHT

1. **Last Move** (Nước đi vừa rồi): Màu vàng nhạt
2. **Valid Moves** (Nước đi hợp lệ): Chấm tròn màu xanh
3. **Selected Piece** (Quân cờ đang chọn): Viền màu xanh lá
4. **Check** (Vua bị chiếu): Màu đỏ

**File xử lý**: `src/main/java/nhom16oop/ui/board/ChessTile.java`

---

## 11. NHỮNG ĐIỀU CẦN BIẾT KHI BÁO CÁO

### KIẾN THỨC CƠ BẢN VỀ OOP ĐƯỢC ÁP DỤNG:

1. **Inheritance (Kế thừa)**:
   - `ChessPiece` là lớp cha
   - `King`, `Queen`, `Rook`, `Bishop`, `Knight`, `Pawn` kế thừa từ `ChessPiece`

2. **Polymorphism (Đa hình)**:
   - Mỗi quân cờ override hàm `generateValidMoves()`
   - Gọi `piece.generateValidMoves()` sẽ chạy code của quân tương ứng

3. **Encapsulation (Đóng gói)**:
   - `ChessPieceMap` quản lý vị trí quân cờ
   - Không cho phép truy cập trực tiếp, chỉ qua getter/setter

4. **Interface**:
   - `GameStateListener`: Lắng nghe thay đổi trạng thái game
   - `TimerListener`: Lắng nghe sự kiện từ timer

### CÁC DESIGN PATTERN:

1. **MVC (Model-View-Controller)**:
   - Model: `ChessPiece`, `ChessPieceMap`, `BoardState`
   - View: `ChessUI`, `ChessBoardUI`, `ChessTile`
   - Controller: `ChessController`, `GameActionManager`

2. **Observer Pattern**:
   - `GameStateListener` để cập nhật UI khi game thay đổi

3. **Strategy Pattern**:
   - `HumanPlayer` vs `StockfishPlayer` implement cùng interface

---

## CHECKLIST TRƯỚC KHI BÁO CÁO

### Kiến thức cơ bản:
- [ ] Hiểu rõ cấu trúc thư mục project
- [ ] Biết cách rebuild project (Build > Rebuild Project hoặc Ctrl+F9)
- [ ] Biết cách run project (Run > Run 'ChessGame' hoặc Shift+F10)
- [ ] Làm quen với cách comment code (Ctrl+/ hoặc Ctrl+Shift+/)

### File quan trọng cần nắm:
- [ ] **Core data structures**: `ChessPosition.java` & `ChessMove.java` (KHÔNG SỬA - core structure)
- [ ] **Base class quân cờ**: `src/main/java/nhom16oop/core/model/ChessPiece.java` (Lớp cha trừu tượng)
- [ ] **Quân cờ**: `src/main/java/nhom16oop/core/pieces/*.java` (King, Queen, Rook, Bishop, Knight, Pawn)
- [ ] **Board State**: `src/main/java/nhom16oop/core/model/BoardState.java` (Quản lý trạng thái bàn cờ)
- [ ] **Timer**: `src/main/java/nhom16oop/game/ChessTimer.java`
- [ ] **Controller**: `src/main/java/nhom16oop/game/ChessController.java`
- [ ] **UI**: `src/main/java/nhom16oop/ui/ChessUI.java`
- [ ] **Toolbar**: `src/main/java/nhom16oop/ui/components/panels/ChessToolbar.java`
- [ ] **Constants**: `src/main/java/nhom16oop/constants/GameConstants.java`
- [ ] **Logic game**: `src/main/java/nhom16oop/utils/BoardUtils.java` (KHÔNG SỬA)

### Kỹ năng cần có:
- [ ] Biết modify nước đi 1 quân cờ (ví dụ: King đi như Rook)
- [ ] Biết cách tắt kiểm tra chiếu vua (trong ChessPiece.java)
- [ ] Biết cách tắt nhập thành hoặc en passant (trong BoardState.java)
- [ ] Biết cách ẩn/hiện timer
- [ ] Biết cách thay đổi thời gian timer
- [ ] Biết cách ẩn/hiện nút trên toolbar
- [ ] Biết cách thay đổi kích thước bàn cờ

### Kiến thức OOP cần nhớ:
- [ ] Biết class nào kế thừa class nào (ChessPiece là cha của King, Queen, v.v.)
- [ ] Hiểu về Polymorphism (mỗi quân override `generateValidMoves()`)
- [ ] Hiểu về MVC pattern trong project
- [ ] Biết về Observer pattern (GameStateListener)

### Test trước khi báo cáo:
- [ ] Đã test ít nhất 2-3 modify đơn giản
- [ ] Project chạy được không lỗi
- [ ] Hiểu tại sao code hoạt động (để trả lời câu hỏi của thầy)

---

## CÂU HỎI THƯỜNG GẶP

**Q: Modify nước đi 1 quân cờ có làm lỗi code khác không?**  
A: KHÔNG. Mỗi quân cờ độc lập, chỉ cần sửa hàm `generateValidMoves()` trong file quân đó.

**Q: Sau khi sửa cần làm gì?**  
A: Rebuild project (Ctrl+F9) rồi chạy lại (Shift+F10)

**Q: Nếu sửa lỗi thì sao?**  
A: Ctrl+Z hoặc Ctrl+Alt+Z (show history) để undo

**Q: Có cần hiểu sâu về OOP không?**  
A: KHÔNG cần. Chỉ cần biết comment/uncomment code và sửa tham số là đủ. Nhưng nếu thầy hỏi về OOP thì xem phần 9.

**Q: File nào TUYỆT ĐỐI KHÔNG NÊN SỬA?**  
A: 
- `BoardUtils.java` (logic chính của game - chiếu tướng, checkmate, v.v.)
- `ChessPieceMap.java` (quản lý vị trí quân cờ)

**Q: Timer hiện UI nhưng không chạy thì sao?**  
A: Kiểm tra trong `ChessController` xem mode đó có gọi `initializeTimer()` không. Nếu không gọi thì timer không chạy dù UI có hiện.

**Q: Làm sao biết timer đang bật hay tắt?**  
A: 
- Timer BẬT: Có gọi `initializeTimer()` trong setup mode (ví dụ: `setPlayerVsPlayer()`)
- Timer TẮT: Có gọi `hideTimerUI()` trong constructor của `ChessUI.java`

**Q: Khi bị chiếu tướng, tôi có phải di chuyển vua không?**  
A: KHÔNG BẮT BUỘC. Bạn có thể:
- Di chuyển vua ra khỏi vị trí bị chiếu
- Chặn bằng quân khác vào giữa
- Ăn quân địch đang chiếu vua
Đây là luật cờ vua chuẩn quốc tế.

**Q: Highlight các ô gợi ý được vẽ ở đâu?**  
A: Trong `ChessTile.java` (hàm `paintComponent()`) và `ChessBoardUI.java` (hàm `generateAndHighlightValidMoves()`).

**Q: Hint sử dụng thuật toán gì?**  
A: Sử dụng **Stockfish engine** - một AI cờ vua mạnh nhất thế giới, KHÔNG phải tự viết thuật toán.

**Q: Có thể thay đổi thời gian cho từng mode khác nhau không?**  
A: CÓ. Sửa số phút trong `initializeTimer()` ở từng hàm setup mode (`setPlayerVsPlayer()`, `setPlayerVsAI()`, v.v.).

**Q: Làm sao ẩn/hiện nút chỉ trong 1 mode cụ thể?**  
A: Thay đổi điều kiện trong tham số cuối của `ButtonConfig`. Ví dụ: `() -> chessController.getGameMode() == GameMode.PUZZLE_MODE`

**Q: Sau khi rebuild vẫn không thấy thay đổi?**  
A: Thử:
1. Clean project (Build > Clean Project)
2. Rebuild lại (Build > Rebuild Project)
3. Hoặc xóa thư mục `target/` rồi rebuild

**Q: Có thể thay đổi kích thước quân cờ không?**  
A: CÓ. Quân cờ tự động scale theo `SQUARE_SIZE`. Thay đổi `SQUARE_SIZE` trong `GameConstants.java` sẽ thay đổi cả kích thước quân cờ.

**Q: Logic ăn quân En Passant ở đâu?**  
A: Trong `Pawn.java` (dòng ~63-72) và `BoardUtils.simulateMove()` (dòng ~109).

**Q: Có thể thêm mode mới không?**  
A: KHÔNG NÊN trong buổi báo cáo vì phức tạp. Chỉ nên modify các mode có sẵn.

**Q: Làm sao đổi font cho toàn bộ UI?**  
A: Tìm tất cả `setFont()` trong project (Ctrl+Shift+F) và thay đổi Font family. Nhưng khuyến nghị chỉ đổi cho từng component cụ thể.

**Q: Emoji không hiển thị đúng?**  
A: Xóa emoji đi và chỉ dùng text thuần. Emoji có thể gây lỗi trên một số font hoặc OS.

**Q: Text tiếng Việt bị lỗi font?**  
A: Đảm bảo dùng font hỗ trợ Unicode như Arial, Georgia, Roboto. Tránh dùng font cũ như MS Sans Serif.

**Q: Thay đổi font có cần rebuild không?**  
A: CÓ. Luôn rebuild (Ctrl+F9) sau khi thay đổi font hoặc text.

**Q: ChessPiece.java là file gì?**  
A: Đây là **abstract base class** (lớp cha trừu tượng) cho TẤT CẢ quân cờ. King, Queen, Rook, Bishop, Knight, Pawn đều kế thừa từ ChessPiece.

**Q: Làm sao tắt kiểm tra chiếu vua (cho phép đi nước để vua bị chiếu)?**  
A: Trong file `ChessPiece.java`, hàm `isValidMove()`, comment phần kiểm tra `isKingInCheck()` và chỉ giữ lại phần kiểm tra nước đi có trong danh sách hay không. Chi tiết xem mục 5.

**Q: Tắt kiểm tra chiếu vua có ảnh hưởng đến các quân khác không?**  
A: CÓ - vì tất cả quân đều kế thừa từ ChessPiece và dùng chung hàm `isValidMove()`. Đây là ví dụ về **Inheritance** trong OOP.

**Q: Sự khác nhau giữa `generateValidMoves()` và `isValidMove()` là gì?**  
A: 
- `generateValidMoves()`: Tạo danh sách TẤT CẢ nước đi có thể (theo luật di chuyển của quân)
- `isValidMove()`: Kiểm tra 1 nước đi cụ thể có hợp lệ không (bao gồm cả kiểm tra chiếu vua)

**Q: Tại sao mỗi quân cờ có `generateValidMoves()` khác nhau?**  
A: Vì đây là **abstract method** - mỗi quân override và implement logic di chuyển riêng. Đây là ví dụ về **Polymorphism**.

**Q: BoardState.java dùng để làm gì?**  
A: Lưu trữ "ảnh chụp màn hình" của bàn cờ tại 1 thời điểm, bao gồm vị trí quân, quyền nhập thành, en passant, lượt chơi. Dùng cho save/load game, undo/redo.

**Q: Làm sao tắt nhập thành (castling)?**  
A: Comment code trong hàm `setLastMove()` của `BoardState.java`. Chi tiết xem mục 4B.

**Q: ChessPosition.java là file gì?**  
A: Đại diện cho **1 ô trên bàn cờ** với tọa độ (col, row). Ví dụ: "e4" = ChessPosition(4, 3). Được dùng làm **key trong Map**, lưu vị trí quân cờ.

**Q: ChessMove.java là file gì?**  
A: Đại diện cho **1 nước đi** với 2 ChessPosition (start, end). Ví dụ: "e2e4" = ChessMove(e2, e4). Được dùng để tạo danh sách nước đi hợp lệ.

**Q: Sự khác nhau giữa ChessPosition và ChessMove?**  
A: ChessPosition = 1 ô trên bàn cờ. ChessMove = 1 nước đi (từ ô này đến ô khác). ChessMove SỬ DỤNG 2 ChessPosition.

**Q: Tại sao KHÔNG NÊN sửa ChessPosition và ChessMove?**  
A: Vì đây là **core data structures** được dùng MỌI NƠI trong project. Thay đổi sẽ phá vỡ toàn bộ game. Record class đã tối ưu, không cần thêm gì.

**Q: ChessPosition có cache là gì?**  
A: ChessPosition tạo sẵn 64 object cho tất cả ô a1→h8. Khi gọi `get("e4")` không tạo object mới mà lấy từ cache → tiết kiệm bộ nhớ và nhanh hơn.

---

## TIPS KHI DEMO/BÁO CÁO

1. **Chuẩn bị trước**: Test tất cả modify trước khi báo cáo
2. **Giải thích OOP**: Biết được class nào kế thừa class nào (xem phần 10)
3. **Nhấn mạnh tính độc lập**: Mỗi quân cờ là 1 object độc lập
4. **Hiểu về MVC**: Biết phân biệt Model, View, Controller
5. **Chuẩn bị 2-3 modify đơn giản**: Ví dụ như đổi thời gian timer, ẩn nút, thay đổi giá trị quân
6. **Không sửa BoardUtils.java**: Nhấn mạnh đây là core logic không nên động đến

---

## GHI CHÚ

- Ưu tiên **COMMENT code cũ** thay vì xóa
- **KHÔNG** tạo thêm hàm mới, chỉ sửa code trong hàm có sẵn
- Test trước khi báo cáo
- Lưu ý: Sau mỗi thay đổi **phải rebuild** project

---

**Cập nhật cuối**: 30/11/2025  
**Người tạo**: Nhóm 16 OOP

