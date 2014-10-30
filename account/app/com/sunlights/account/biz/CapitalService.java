package com.sunlights.account.biz;

import java.util.List;

import com.sunlights.account.facade.model.Capital4Product;
import com.sunlights.account.facade.model.TotalCapitalInfo;

/**
 * 
 * @author tangweiqun 2014/10/22
 *
 */
public interface CapitalService {
	
	public TotalCapitalInfo getTotalCapital(String custId, boolean takeCapital4Prd);
	
	public List<Capital4Product> getAllCapital4Product(String custId);
	
}