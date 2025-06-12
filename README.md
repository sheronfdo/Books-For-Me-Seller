# Books-For-Me-Seller 📚🛍️
[![Ask DeepWiki](https://devin.ai/assets/askdeepwiki.png)](https://deepwiki.com/sheronfdo/Books-For-Me-Seller)

**Books-For-Me-Seller** is an Android application designed for book vendors to manage their bookstore on the Books-For-Me platform. The app provides a streamlined interface for sellers to list, update, and manage books, process orders, and maintain their shop profile—all from a mobile device. Whether you're a professional bookstore or an independent seller, this app gives you the tools to reach more readers and grow your business 📈.

---

## Features ✨

* **Seller Authentication:**
  * Secure login via Google Sign-In using Firebase Authentication.
  
* **Book Management:**
  * Add new books with details like title, author, price, condition, description, cover image, tags, language, and publication year.
  * Edit or delete existing book listings.
  * Upload and preview cover images using camera or gallery.
  * Book image caching for faster load times.

* **Inventory Dashboard:**
  * View all active, inactive, or sold-out books.
  * Get real-time status on listed books.

* **Order Management:**
  * Receive and manage incoming orders from customers.
  * View buyer details and shipping address.
  * Update order status (Order Confirmed → Processing → Shipped → Delivered).
  * Visual timeline for order tracking.

* **Seller Profile Management:**
  * Update personal and store information: display name, contact details, and profile image.
  * Change profile picture using camera or gallery.
  * Real-time sync with Firebase for instant updates.

* **Analytics & Insights:**
  * View basic analytics on total books listed, total orders received, and completed sales.
  * Insights on top-performing listings.

* **Push Notifications:**
  * Receive instant notifications about new orders, shipping status updates, and important messages via Firebase Cloud Messaging.

* **Theme Preferences:**
  * Choose between Light Mode, Dark Mode, or System Default theme 🌗.

---

## Tech Stack & Dependencies 🛠️

* **Platform:** Android (Java)
* **Backend & Services:**
  * Firebase:
    * Authentication (Google Sign-In)
    * Firestore (Real-time database)
    * Storage (Image uploads)
    * Cloud Messaging (Push notifications)
    * Crashlytics (Crash reporting)
* **Networking:**
  * OkHttp (HTTP requests)
  * Gson (JSON serialization/deserialization)
* **Image Handling:**
  * Glide (Image loading and caching)
* **UI Components:**
  * Material Components for Android
  * RecyclerView, CardView
  * Lottie (Animated illustrations)
  * ViewPager2 (Image carousels)
* **Utilities:**
  * ModelMapper (Object mapping between DTOs and entities)

---

## Setup and Installation 🚀

1. **Clone the Repository:**
   ```bash
   git clone https://github.com/sheronfdo/Books-For-Me-Seller.git
   ```

2. **Open in Android Studio:**
   Open the cloned project folder in Android Studio.

3. **Firebase Configuration:**
   * Download your `google-services.json` file from Firebase Console.
   * Place it inside the `app/` directory.

4. **API Keys Configuration:**
   * **Google Maps API Key (if used):**
     Open `app/src/main/AndroidManifest.xml` and add your API key:
     ```xml
     <meta-data
         android:name="com.google.android.geo.API_KEY"
         android:value="YOUR_GOOGLE_MAPS_API_KEY" />
     ```
     For cloud-based styling:
     ```xml
     <meta-data
         android:name="com.google.android.geo.MAP_ID"
         android:value="YOUR_MAP_ID_IF_USING_CLOUD_STYLING" />
     ```

5. **Backend URL Configuration:**
   Update the API base URL in `app/src/main/java/com/jamith/booksformeseller/util/UrlConstants.java`:
   ```java
   public static final String BASE_URL = "YOUR_BACKEND_API_BASE_URL"; // e.g., http://192.168.1.100:8080/api
   ```

6. **Build and Run:**
   * Sync Gradle in Android Studio.
   * Build and run the app on an Android device or emulator.

---

## Contributing 🤝

Contributions are welcome! If you find a bug or want to propose a new feature, feel free to open an issue or submit a pull request.

---

## License 📄

This project is licensed under the [MIT License](LICENSE).

---

## Connect 🌐

Have questions or want to connect?

* GitHub: [sheronfdo](https://github.com/sheronfdo)
* Ask via [DeepWiki](https://deepwiki.com/sheronfdo/Books-For-Me-Seller)

---

Happy Selling! 📚🚀
```

Let me know if you'd like to customize it further for documentation, screenshots, or contribution guidelines!
