# Important Note

This project was designed as a simple Java application without using web services.

---

## **Running the Project**
### **Prerequisites**
- **Java 21** installed
- **Maven** installed

### **Build & Run**
1. **Clone the repository:**
   ```sh
   git clone https://github.com/erkurtharun/shopping-cart.git
   cd shopping-cart
   ```

2. **Build the project:**
   ```sh
   mvn clean install
   ```

3. **Run the application:**
   ```sh
   mvn exec:java -Dexec.mainClass="com.shoppingcart.Main" -Dexec.args="src/main/input.json src/main/output.json"
   ```

4. **Run tests:**
   ```sh
   mvn test
   ```
   
---

# **Shopping Cart Application (Trendyol Shopping Cart Case)**

This project implements a **shopping cart system** similar to those used in **e-commerce platforms**. The application processes commands from an input file, executes them sequentially, and records the results in an output file.

The implementation focuses on **clean code principles**, **SOLID design**, **object-oriented programming**, and **testability**. It has been designed to be **easily extendable** and follows **Domain-Driven Design (DDD)** concepts. Additionally, the project has been developed with **Test-Driven Development (TDD)** practices to ensure high-quality code.

---

## **Cart Functionality**
The **Cart** serves as the central object in the system and manages all the items and associated operations. The cart has the following constraints:

- It can contain up to **10 unique items** (excluding value-added service items).
- The total number of items cannot exceed **30**.
- The total cost (including all items and additional services) cannot exceed **500,000 TL**.
- The **total price** is the sum of all item prices, including value-added service items.
- The **total amount** is calculated as:
  ```plaintext
  totalAmount = totalPrice - totalDiscount
  ```

---

## **Item Types**
### **1️⃣ Standard Items**
Items are the main products added to the cart. They have a **seller ID** and a **category ID**. Items can be:
- **Added** or **removed** from the cart.
- **Reset** to clear all items.

Each item type has specific **rules** and **restrictions** on how it can be used.

### **2️⃣ Digital Items**
Digital items include **virtual products** such as **gift cards** and **donation cards**. Key constraints:
- A maximum of **5** digital items can be added to the cart.
- Digital items always have **Category ID: 7889**.
- No other item type can be assigned this category.

### **3️⃣ Default Items**
Default items represent **physical goods**, such as **electronics, clothing, and home products**.
- If a value-added service is attached to a default item, the service's price **cannot exceed** the price of the default item itself.

### **4️⃣ Value-Added Service (Vas) Items**
Value-added service (VAS) items represent **extra services** such as **insurance, assembly, and extended warranties**.
- These items are **not physical products** and must be **attached to a DefaultItem**.
- VAS items can **only** be added to items in:
    - **Furniture (Category ID: 1001)**
    - **Electronics (Category ID: 3004)**
- Up to **3 VasItems** can be added to a single DefaultItem.
- VasItems always have:
    - **Category ID: 3242**
    - **Seller ID: 5003**
- No other item type can use **Seller ID: 5003**.

---

## **Promotions & Discounts**
The system supports **discount rules** that are automatically applied based on cart contents.

### **1️⃣ Same Seller Discount**
- **Discount ID: 9909**
- If all items (excluding VasItems) are from the **same seller**, a **10% discount** is applied.

### **2️⃣ Category-Based Discount**
- **Discount ID: 5676**
- A **5% discount** applies to items in **Category ID: 3003**.
- This discount is applied **individually to each item** in the category.

### **3️⃣ Total Price-Based Discount**
- **Discount ID: 1232**
- Discounts are based on the cart's **total price**:
    - **500 TL – 4,999 TL** → **250 TL discount**
    - **5,000 TL – 9,999 TL** → **500 TL discount**
    - **10,000 TL – 49,999 TL** → **1,000 TL discount**
    - **50,000 TL+** → **2,000 TL discount**
- **VasItems are included in total price calculations.**
- **Only one promotion can be applied at a time** (whichever provides the highest discount).

---

## **How It Works**
1. The application **reads commands** from an input file.
2. Each command is **processed sequentially**.
3. The results of each operation are **written to an output file**.

The system is built to be **modular, extendable, and testable**, ensuring smooth integration of future features.

---

## **Development Guidelines**
- **Clean Code**: The project follows **SOLID** principles for maintainability.
- **Test-Driven Development (TDD)**: Every feature is **unit tested**.
- **Domain-Driven Design (DDD)**: The architecture is structured around **business rules**.

---

# Commands  
Below are the commands that can be used in the input file that your application will receive from the command line and the outputs that it will write to the output file.

**Input**  
```  
{"command":"addItem","payload":{"itemId":int,"categoryId":int,"sellerId":int,"price":double,"quantity":int}}  
```  
**Output:**  
```  
{"result":boolean, "message": string}  
```  
**Input**  
```  
{"command":"addVasItemToItem", "payload": {"itemId": int, "vasItemId":int, "vasCategoryId": int, "vasSellerId":int, "price":double, "quantity":int}}  
```  
**Output:**  
```  
{"result":boolean, "message": string}  
```  
**Input**  

Deletes item with it's quantity and VasItems'
```  
{"command":"removeItem", "payload":{"itemId":int}}  
```  
**Output:**  
```  
{"result":boolean, "message": string}  
```  
**Input**  
```  
{"command":"resetCart"}  
```  
**Output:**  
```  
{"result":boolean, "message": string}  
```  
**Input**  
```  
{"command":"displayCart"}  
```  
**Output:**  
```  
{"result":boolean, "message":{"items":[ty.item], "totalAmount":double, "appliedPromotionId":int, "totalDiscount":double}}  
ty.item -> {"itemId": int, "categoryId": int, "sellerId":int, "price":double, "quantity":int, "vasItems":[ty.vasItem]}  
ty.vasItem -> {"vasItemId":int, "vasCategoryId": int, "vasSellerId":int, "price":double, "quantity":int}  
```
