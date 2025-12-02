# Fly.io Backend Troubleshooting

## Current Issue
Backend at https://jose-long-morning-2431.fly.dev is timing out / not responding

## Quick Diagnosis Steps

### 1. Check App Status
```bash
cd My-Personal-Library-Back
fly status
```

This will show:
- App state (running, stopped, crashed)
- Number of instances
- Resource usage

### 2. Check Logs
```bash
fly logs
```

Look for:
- Error messages
- Database connection errors
- Application crashes
- Startup issues

### 3. Check if App is Running
```bash
fly ps
```

Shows running instances

## Common Causes & Fixes

### Cause 1: App is Sleeping (Free Tier)
**Symptoms:** First request after inactivity times out (10-30 seconds)

**Solution:**
```bash
# Restart the app
fly restart

# Or wake it up with a request
curl https://jose-long-morning-2431.fly.dev/api/books
```

**Long-term Fix:**
- Upgrade to paid plan (no sleep)
- Use keep-alive service (UptimeRobot, etc.)

### Cause 2: App Crashed
**Symptoms:** App shows as "stopped" or "crashed" in `fly status`

**Check:**
```bash
fly logs --app jose-long-morning-2431
```

**Fix:**
```bash
# Restart
fly restart

# If still failing, check logs for errors
fly logs
```

### Cause 3: Database Connection Failed
**Symptoms:** Logs show database connection errors

**Check:**
```bash
# Verify database secrets are set
fly secrets list

# Should show:
# DB_URL
# DB_USER
# DB_PW
```

**Fix:**
```bash
# Set database secrets if missing
fly secrets set DB_URL=postgres://...
fly secrets set DB_USER=your_user
fly secrets set DB_PW=your_password
```

### Cause 4: Out of Memory
**Symptoms:** App crashes, logs show OOM errors

**Check:**
```bash
fly status
# Look at memory usage
```

**Fix:**
```bash
# Increase memory
fly scale memory 512
```

### Cause 5: Port Configuration
**Symptoms:** App runs but can't be reached

**Check:**
```bash
fly config show
# Verify internal_port is 8080
```

**Fix:**
- Ensure `application.yml` has `server.port: 8080`
- Verify `fly.toml` has correct port mapping

## Emergency Restart

If nothing else works:

```bash
# Stop the app
fly apps destroy jose-long-morning-2431

# Redeploy (if you have the code)
fly deploy
```

## Check Fly.io Dashboard

1. Go to: https://fly.io/dashboard
2. Select your app: **jose-long-morning-2431**
3. Check:
   - **Status** tab: App state
   - **Metrics** tab: CPU, Memory, Requests
   - **Logs** tab: Recent log entries
   - **Secrets** tab: Environment variables

## Verify Database is Accessible

If using Fly.io Postgres:

```bash
# Check database status
fly postgres list

# Connect to database
fly postgres connect -a your-db-name
```

## Quick Health Check Script

Create a simple endpoint to check health:

```java
@GetMapping("/health")
public ResponseEntity<String> health() {
    return ResponseEntity.ok("OK");
}
```

Then test:
```bash
curl https://jose-long-morning-2431.fly.dev/health
```

## Next Steps

1. **Run:** `fly status` - Check app state
2. **Run:** `fly logs` - Check for errors
3. **If crashed:** `fly restart`
4. **If sleeping:** Wait 10-30 seconds for first request, or restart
5. **If database issue:** Check secrets and database status

## Prevention

1. **Set up monitoring:**
   - Use Fly.io metrics
   - Set up alerts

2. **Keep app alive:**
   - Upgrade to paid plan
   - Use keep-alive service

3. **Monitor logs:**
   - Check logs regularly
   - Set up log aggregation

