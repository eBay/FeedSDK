package com.ebay.feed.enums;

/**
 * <p>
 * Enumerates the types of supported environments
 * </p>
 * @author skumaravelayutham
 *
 */
public enum EnvTypeEnum {
	SANDBOX,
	PRODUCTION;
	
	public static EnvTypeEnum getEnvEnum(String name){
		for(EnvTypeEnum envEnum: EnvTypeEnum.values()){
			if(envEnum.name().equalsIgnoreCase(name)){
				return envEnum;
			}
		}
		return null;
	}
}
