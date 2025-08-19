Program Summary

CafeForGeeks is a basic Java based cafe management system. It implements a menu-driven program where a user can:

1) View the Product List: Display all food items offered at the cafe including combos.
2) Place Orders: Select a specified quantity of a food item. Place the order by paying the amount.
3) Track and Update Muffin Inventory: Track the inventory for muffins, since we keep a stock of the muffins in our store.
4) Generate Sales Reports: Display a report with the summaries of total items ordered and the total revenue generated.
5) Update Prices: There is an option to update the price of a food item.
6) Exit the Program: Safely exit the program.

Input is validated everywhere to handle invalid choices and to prevent the program from crashing.
Note: Combos are considered as a product of their own and hence the sales report would not be showing an incremented base product quantity for combos. This is to keep the integrity of the sales report and the program.

Compiling and running the program from the terminal:

1) Navigate to the source code folder and run:

		javac <packageName>/*.java
		
2) After compilation,run the main class with:

		java <packageName>.Main
		
Replace <packageName> with the actual name of the package ( In my codebase the package is named "cafe")

Video Recording Link : https://rmiteduau-my.sharepoint.com/:v:/r/personal/s4112130_student_rmit_edu_au/Documents/Recording-20250819_132157.webm?csf=1&web=1&e=udPx5R&nav=eyJyZWZlcnJhbEluZm8iOnsicmVmZXJyYWxBcHAiOiJTdHJlYW1XZWJBcHAiLCJyZWZlcnJhbFZpZXciOiJTaGFyZURpYWxvZy1MaW5rIiwicmVmZXJyYWxBcHBQbGF0Zm9ybSI6IldlYiIsInJlZmVycmFsTW9kZSI6InZpZXcifX0%3D

References:

[1] GeeksforGeeks, “BigDecimal Class in Java,” 2025. [Online]. Available:
https://www.geeksforgeeks.org/java/bigdecimal-class-java/

[2] GeeksforGeeks, “Scanner Class in Java,” 2025. [Online]. Available:
https://www.geeksforgeeks.org/java/scanner-class-in-java/

[3] GeeksforGeeks, “HashMap in Java (java.util.HashMap) with Examples,” 2025. [Online]. Available:
https://www.geeksforgeeks.org/java/java-util-hashmap-in-java-with-examples/

[4] GeeksforGeeks, “enum in Java,” 2024. [Online]. Available:
https://www.geeksforgeeks.org/java/enum-in-java/

[5] GeeksforGeeks, “BigDecimal setScale() Method in Java (rounding),” 2025. [Online]. Available:
https://www.geeksforgeeks.org/java/bigdecimal-setscale-method-in-java-with-examples/

[6] GeeksforGeeks, “NumberFormat setRoundingMode() Method in Java,” 2025. [Online]. Available:
https://www.geeksforgeeks.org/java/numberformat-setroundingmode-method-in-java-with-examples/