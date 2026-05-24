# 🔐 Google OAuth2 Setup Guide for Banking System

This guide will help you set up Google OAuth2 authentication for the Banking System application.

## Prerequisites
- A Google Cloud Project
- Access to [Google Cloud Console](https://console.cloud.google.com)
- The application running on `http://localhost:8082`

## Step 1: Create a Google Cloud Project

1. Go to [Google Cloud Console](https://console.cloud.google.com)
2. Click on the project dropdown at the top
3. Click "NEW PROJECT"
4. Enter a project name: `Banking System OAuth`
5. Click "CREATE"
6. Wait for the project to be created and select it

## Step 2: Enable Google+ API

1. In the left sidebar, go to **APIs & Services** → **Library**
2. Search for "Google+ API"
3. Click on it and press **ENABLE**
4. Wait for it to enable

## Step 3: Create OAuth2 Credentials

### 3a. Configure the OAuth Consent Screen
1. Go to **APIs & Services** → **OAuth consent screen**
2. Select **External** as the User Type
3. Click **CREATE**
4. Fill in the following:
   - **App name**: `Banking System`
   - **User support email**: Your email address
   - **Developer contact information**: Your email address
5. Click **SAVE AND CONTINUE**
6. On the Scopes page, click **SAVE AND CONTINUE** (default scopes are fine)
7. On the Summary page, click **SAVE AND CONTINUE**

### 3b. Create OAuth2 Client Credentials
1. Go to **APIs & Services** → **Credentials**
2. Click **+ CREATE CREDENTIALS** → **OAuth client ID**
3. Select **Web application**
4. Under "Authorized JavaScript origins", click **+ ADD URI** and add:
   - `http://localhost:8082`
5. Under "Authorized redirect URIs", click **+ ADD URI** and add:
   - `http://localhost:8082/login/oauth2/code/google`
6. Click **CREATE**
7. Copy the **Client ID** and **Client Secret**

## Step 4: Configure Your Application

1. **Copy the `.env.template` file to `.env`:**
   ```bash
   cp .env.template .env
   ```

2. **Edit the `.env` file** and replace the placeholders:
   ```
   GOOGLE_CLIENT_ID=YOUR_CLIENT_ID_HERE.apps.googleusercontent.com
   GOOGLE_CLIENT_SECRET=YOUR_CLIENT_SECRET_HERE
   APPLICATION_REDIRECT_URI=http://localhost:8082/login/oauth2/code/google
   ```

   - Replace `YOUR_CLIENT_ID_HERE` with the Client ID from Step 3b
   - Replace `YOUR_CLIENT_SECRET_HERE` with the Client Secret from Step 3b

3. **Make sure the `.env` file is NOT committed to version control:**
   - Check your `.gitignore` file includes: `.env`

## Step 5: Run the Application

1. **Build the project:**
   ```bash
   mvn clean install
   ```

2. **Start the application:**
   ```bash
   mvn spring-boot:run
   ```

3. **Open your browser** and navigate to:
   ```
   http://localhost:8082
   ```

## Step 6: Test Google OAuth Login

1. Click on "Sign in with Google" button on the login page
2. Enter your Google account credentials
3. Grant permission for the application to access your profile
4. You should be redirected to the dashboard

## Features After OAuth Login

✅ **Automatic Account Creation**: If it's your first time logging in with Google, a new customer account is created automatically

✅ **Profile Information**: Your name and email from Google are pre-filled

✅ **Session Management**: Your session is maintained throughout the application

✅ **Loan Application**: You can immediately apply for loans

✅ **EMI Management**: View and pay your EMIs

## Troubleshooting

### Issue: "redirect_uri_mismatch" Error
- **Solution**: Make sure the Authorized redirect URIs in Google Cloud Console exactly matches `http://localhost:8082/login/oauth2/code/google`

### Issue: OAuth not working after deploying to production
- **Solution**: Update your `.env` file with production URLs and update Google Cloud Console:
  - Authorized JavaScript origins: `https://yourdomain.com`
  - Authorized redirect URIs: `https://yourdomain.com/login/oauth2/code/google`

### Issue: "Client authentication failed" 
- **Solution**: Make sure your `GOOGLE_CLIENT_ID` and `GOOGLE_CLIENT_SECRET` are correct in the `.env` file

### Issue: `.env` file not being loaded
- **Solution**: Ensure the `.env` file is in the project root directory (same location as `pom.xml`)

## Production Deployment

When deploying to production:

1. **Update environment variables** in your deployment platform (Heroku, AWS, Azure, etc.)
2. **Set the correct redirect URIs** in Google Cloud Console
3. **Use HTTPS** instead of HTTP
4. **Never commit the `.env` file** to version control

### Example environment variables for deployment:
```
GOOGLE_CLIENT_ID=your-production-client-id.apps.googleusercontent.com
GOOGLE_CLIENT_SECRET=your-production-client-secret
APPLICATION_REDIRECT_URI=https://yourdomain.com/login/oauth2/code/google
```

## Additional Resources

- [Spring Boot OAuth2 Documentation](https://spring.io/projects/spring-security-oauth2)
- [Google OAuth2 Documentation](https://developers.google.com/identity/protocols/oauth2)
- [Google Cloud Console](https://console.cloud.google.com)

## Security Notes

⚠️ **Important**: 
- Never share your `GOOGLE_CLIENT_SECRET` publicly
- Never commit `.env` file to version control
- Use environment variables in production instead of `.env` files
- Always use HTTPS in production
- Regularly rotate your OAuth credentials if compromised

---

**Need help?** Contact the development team or check the application logs for detailed error messages.
