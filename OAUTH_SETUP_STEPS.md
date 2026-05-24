# 🔐 Google OAuth Setup - Step by Step

## Follow These Steps Exactly

### **STEP 1: Create a Google Cloud Project**

1. Go to: https://console.cloud.google.com/
2. Click on the **Project** dropdown (top left)
3. Click **NEW PROJECT**
4. Enter Project name: `BankingSystem`
5. Click **CREATE**
6. Wait for project to be created (1-2 minutes)

---

### **STEP 2: Enable Google+ API**

1. In the search bar at the top, search: `Google+ API`
2. Click on **Google+ API** from results
3. Click **ENABLE**
4. Wait for it to enable

---

### **STEP 3: Create OAuth 2.0 Credentials**

1. Go to: https://console.cloud.google.com/apis/credentials
2. Click **CREATE CREDENTIALS**
3. Select **OAuth 2.0 Client IDs**
4. You'll see a message: *"To create an OAuth 2.0 client ID, you must first set up the OAuth consent screen"*
5. Click **CREATE CONSENT SCREEN**

---

### **STEP 4: Configure OAuth Consent Screen**

1. Select **External** (for testing)
2. Click **CREATE**
3. Fill in:
   - **App name**: `Banking System`
   - **User support email**: Your Google account email
   - **Developer contact**: Your Google account email
4. Click **SAVE AND CONTINUE**
5. Click **SAVE AND CONTINUE** on Scopes page
6. Click **SAVE AND CONTINUE** on Test users page
7. Click **BACK TO DASHBOARD** at the end

---

### **STEP 5: Create OAuth Application Credentials**

1. Go to: https://console.cloud.google.com/apis/credentials
2. Click **CREATE CREDENTIALS**
3. Select **OAuth 2.0 Client IDs**
4. Select **Web application**
5. Fill in **Name**: `Banking System Client`
6. Under **Authorized JavaScript origins**, add:
   ```
   http://localhost:8082
   ```
7. Under **Authorized redirect URIs**, add:
   ```
   http://localhost:8082/login/oauth2/code/google
   ```
8. Click **CREATE**

---

### **STEP 6: Copy Your Credentials**

A modal will appear with your credentials:
- **Client ID** (long string like: `123456789-abc...apps.googleusercontent.com`)
- **Client Secret** (long string)

**👉 COPY BOTH VALUES - YOU NEED THEM NEXT!**

---

### **STEP 7: Add to .env File**

Open your project folder and find `.env` file in the root:

```bash
D:\MyProjects\BankingSystem\.env
```

Edit it and add your credentials:

```env
GOOGLE_CLIENT_ID=YOUR_CLIENT_ID_HERE
GOOGLE_CLIENT_SECRET=YOUR_CLIENT_SECRET_HERE
APPLICATION_REDIRECT_URI=http://localhost:8082/login/oauth2/code/google
```

**Example:**
```env
GOOGLE_CLIENT_ID=123456789-abcdefghijk.apps.googleusercontent.com
GOOGLE_CLIENT_SECRET=GOCSPX-1234567890abcdefgh
APPLICATION_REDIRECT_URI=http://localhost:8082/login/oauth2/code/google
```

---

### **STEP 8: Save and You're Done! ✅**

Your credentials are now configured!

---

## ✨ Quick Troubleshooting

### **I see "Invalid origin" error**
- Remove `https://` or `http://` - just use domain with port
- Use exactly: `http://localhost:8082`

### **I see "URI must not be empty"**
- Make sure you clicked **+ Add URI** button before entering
- Try refreshing the page

### **Where do I find my credentials again?**
- Go to: https://console.cloud.google.com/apis/credentials
- Look for **OAuth 2.0 Client IDs** section
- Click on your client to see details

---

## 🚀 Next Steps

1. ✅ Get credentials (this guide)
2. ✅ Add to .env file
3. 📝 **Then run your project:**
   ```bash
   mvn clean install -DskipTests
   mvn spring-boot:run
   ```

---

**Need help?** Ask! 💬
