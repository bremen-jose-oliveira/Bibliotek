# Fly.io Secrets Setup Guide

## Current Issue
Backend is crashing because `APPLE_TEAM_ID` and `APPLE_KEY_ID` environment variables are missing.

## Quick Fix

### Set Required Secrets in Fly.io

Run these commands in your backend directory:

```bash
cd My-Personal-Library-Back

# Set Apple OAuth secrets (using values from application.yml)
fly secrets set APPLE_TEAM_ID=YJYA3WJYT5
fly secrets set APPLE_KEY_ID=YV7L248U6W

# Verify secrets are set
fly secrets list
```

### All Required Secrets

Make sure these are all set:

```bash
# Database
fly secrets set DB_URL=your_postgres_url
fly secrets set DB_USER=your_db_user
fly secrets set DB_PW=your_db_password

# Backend/Frontend URLs
fly secrets set BACK_END_URL=https://jose-long-morning-2431.fly.dev
fly secrets set FRONT_END_URL=https://p-lib.netlify.app

# JWT
fly secrets set JWT_SECRET_KEY=your_jwt_secret_key

# SMTP
fly secrets set SMTP_EMAIL=your_smtp_email

# Google OAuth
fly secrets set GOOGLE_CLIENT_SECRET=your_google_client_secret

# Apple OAuth (NEW - this was missing!)
fly secrets set APPLE_TEAM_ID=YJYA3WJYT5
fly secrets set APPLE_KEY_ID=YV7L248U6W
```

## After Setting Secrets

1. **Restart the app:**
   ```bash
   fly restart
   ```

2. **Check logs:**
   ```bash
   fly logs
   ```

3. **Verify it's running:**
   ```bash
   fly status
   ```

## Verify Secrets

```bash
# List all secrets
fly secrets list

# Should show all the variables above
```

## Code Fix Applied

I've also updated `AppleClientSecretProvider.java` to use default values from `application.yml` if environment variables aren't set. This provides a fallback, but it's still better to set them as secrets.

## Next Steps

1. Set the missing secrets: `APPLE_TEAM_ID` and `APPLE_KEY_ID`
2. Restart the app: `fly restart`
3. Check logs: `fly logs` - should see app starting successfully
4. Test: `curl https://jose-long-morning-2431.fly.dev/api/books`

