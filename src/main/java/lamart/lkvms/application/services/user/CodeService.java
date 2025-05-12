package lamart.lkvms.application.services.user;

import java.util.Random;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
public class CodeService {
    private static final  String TELEGRAM_CACHE_PREFIX = "telegram";
    private static final String PASSWORD_RESET = "pwd";

    private final CacheManager cacheManager;
    private final Random random = new Random();

    CodeService(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public String generateConfirmationCode() {
        return String.format("%06d", random.nextInt(900000) + 100000);
    }

    public String createUserConfirmationCode(String username) {
        String confirmationCode = generateConfirmationCode();
        getCache().put(username, confirmationCode);
        return confirmationCode;
    }

    public boolean verifyUserConfirmationCode(String username, String verificationCode) {
        Cache.ValueWrapper wrapper = getCache().get(username);
        return wrapper != null && verificationCode.equals(wrapper.get());
    }

    public String generateGenericCode(int length) {
        return String.format("%0" + length + "d", random.nextInt((int) Math.pow(10, length)));
    }

    public String generateTelegramLinkCode() {
        return generateGenericCode(10);
    }

    public String generatePasswordResetCode() {
        return generateGenericCode(6);
    }

    public String getOrCreateUsersTelegramLinkCode(String username) {
        Cache cache = getTelegramCache();
        Cache.ValueWrapper wrapper = cache.get(username);
        
        if (wrapper != null) {
            String code = (String) wrapper.get();
            saveTelegramCodeInCache(code, username); // Reset timeout
            return code;
        }

        while (true) {
            String code = generateTelegramLinkCode();
            if (cache.get(code) == null) {
                saveTelegramCodeInCache(code, username);
                return code;
            }
        }
    }

    private void saveTelegramCodeInCache(String code, String username) {
        Cache cache = getTelegramCache();
        cache.put(username, code);
        cache.put(code, username);
    }

    public String getUsernameFromTelegramCode(String telegramCode) {
        Cache.ValueWrapper wrapper = getTelegramCache().get(telegramCode);
        return wrapper != null ? (String) wrapper.get() : null;
    }

    public boolean deleteUsersTelegramCode(String username) {
        Cache cache = getTelegramCache();
        Cache.ValueWrapper wrapper = cache.get(username);
        
        if (wrapper != null) {
            String code = (String) wrapper.get();
            cache.evict(username);
            cache.evict(code);
            return true;
        }
        return false;
    }

    public String getOrCreatePasswordResetCode(String email) {
        Cache cache = getPasswordResetCache();
        Cache.ValueWrapper wrapper = cache.get(email);
        
        if (wrapper != null) {
            String code = (String) wrapper.get();
            cache.put(email, code); // Reset timeout
            return code;
        }

        String code = generatePasswordResetCode();
        cache.put(email, code);
        return code;
    }

    public boolean deletePasswordResetCode(String email) {
        return getPasswordResetCache().evictIfPresent(email);
    }

    private Cache getCache() {
        return cacheManager.getCache("default");
    }

    private Cache getTelegramCache() {
        return cacheManager.getCache(TELEGRAM_CACHE_PREFIX);
    }

    private Cache getPasswordResetCache() {
        return cacheManager.getCache(PASSWORD_RESET);
    }
}
