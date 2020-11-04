package kr.com.inspect.dao;

import java.util.List;

import kr.com.inspect.dto.Metadata;
import org.json.simple.JSONObject;

import kr.com.inspect.dto.Sound;

public interface PostgreDao {
	public List<Metadata> getTable();
	public void insertElasticIndex(String index);
	public boolean insertJSONObject(String path);
	public boolean insertXlsxTable(String path);
}
