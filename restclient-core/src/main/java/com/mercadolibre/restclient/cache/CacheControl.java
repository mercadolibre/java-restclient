package com.mercadolibre.restclient.cache;

import com.mercadolibre.restclient.http.Headers;
import com.mercadolibre.restclient.log.LogUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author mlabarinas
 */
public class CacheControl {

	private int age;
	private	int maxAge;
	private	int whileRevalidate;
	private	int ifError;
	
	private long expiration;
	private long created;

	private static final Logger log = LoggerFactory.getLogger(LogUtil.LOGGER_NAME);
	
	public CacheControl(int age, int maxAge, int whileRevalidate, int ifError) {
		this.age = age;
		this.maxAge = maxAge;
		
		if (whileRevalidate >= 0) {
			this.whileRevalidate = whileRevalidate;
		}
		
		if (ifError >= 0) {
			this.ifError = ifError;
		}
	}
	
	public static class Builder {
		
		static final Pattern maxAge = buildPattern("max-age");
		static final Pattern whileRevalidate  = buildPattern("stale-while-revalidate");
		static final Pattern ifError = buildPattern("stale-if-error");
		
		private String cacheControlHeaderValue;
		private String ageHeaderValue;
		
		public Builder(Headers headers) {
			this.cacheControlHeaderValue = headers.contains("Cache-Control") ? headers.getHeader("Cache-Control").getValue() : "";
			this.ageHeaderValue = headers.contains("Age") ? headers.getHeader("Age").getValue() : "";
		}
		
		private static Pattern buildPattern(String field) {
			return Pattern.compile(String.format(".*\\b%s\\s*=\\s*(\\d+).*", field), Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
		}
		
		private int extract(Pattern pattern) {
			Matcher matcher = pattern.matcher(cacheControlHeaderValue);
			
			if (matcher.matches()) {
				String value = matcher.group(1);
				
				try {
					return Integer.parseInt(value);
				} catch(NumberFormatException e) {
					log.error("Cache Control value is not a number: " + value);
				}
			}
			
			return 0;
		}
		
		public CacheControl build() {
			CacheControl cacheControl = new CacheControl(0, 0, 0, 0);
			
			if (StringUtils.isNotBlank(ageHeaderValue) && StringUtils.isNumeric(ageHeaderValue)) {
				cacheControl.setAge(Integer.parseInt(ageHeaderValue));
			}
			
			if (StringUtils.isNotBlank(cacheControlHeaderValue)) {
				cacheControl.setMaxAge(extract(maxAge));
				cacheControl.setWhileRevalidate(extract(whileRevalidate));
				cacheControl.setIfError(extract(ifError));
			}
			
			cacheControl.setExpiration();
			
			return cacheControl;
		}
		
	}
	
	public static Builder builder(Headers headers) {
		return new Builder(headers);
	}
	
	public void setAge(int age) {
		this.age = age;
	}
	
	public long getCurrentAge() {
		long currentAge = System.currentTimeMillis() - created;
		
		return TimeUnit.MILLISECONDS.toSeconds(currentAge);
	}

	public void setMaxAge(int maxAge) {
		this.maxAge = maxAge;
	}

	public void setWhileRevalidate(int whileRevalidate) {
		if (whileRevalidate >= 0) {
			this.whileRevalidate = whileRevalidate;
		}
	}

	public void setIfError(int ifError) {
		if (ifError >= 0) {
			this.ifError = ifError;
		}
	}
	
	public void setExpiration(long expiration) {
		this.expiration = expiration;
	}
	
	public void setExpiration() {
		created = System.currentTimeMillis() - TimeUnit.SECONDS.toMillis(age);
		
		expiration = maxAge <= age ? created : created + TimeUnit.SECONDS.toMillis(maxAge);
	}

	public boolean isFreshForRevalidate() {
		return getCurrentAge() < maxAge + whileRevalidate;
	}

	public boolean isFreshForError() {
		return getCurrentAge() < maxAge + ifError;
	}
	
	public boolean isExpired() {
		return expiration <= System.currentTimeMillis();
	}

}