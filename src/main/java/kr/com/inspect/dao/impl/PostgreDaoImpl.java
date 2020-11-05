package kr.com.inspect.dao.impl;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import kr.com.inspect.dao.PostgreDao;
import kr.com.inspect.dto.Metadata;
import kr.com.inspect.dto.Utterance;
import kr.com.inspect.mapper.PostgreInsertMapper;
import kr.com.inspect.mapper.PostgreSelectMapper;

@Repository
public class PostgreDaoImpl implements PostgreDao {
	@Autowired
	private PostgreInsertMapper postgreInsertMapper;
	
	@Autowired
	private PostgreSelectMapper postgreSelectMapper;
	
	@Autowired
	private SqlSession sqlSession;
	private final String ns = "MetadataMapper.";
	
	/* PostgreSQL에서 Metadata 테이블을 모두 가지고 옴 */
	@Override
	public List<Metadata> getMetadata() {
		return postgreSelectMapper.getMetadata();
	}
	/* PostgreSQL에서 id로 해당되는 Metadata 테이블을 가져옴 */
	@Override
	public Metadata getMetadataById(Integer id){
		return postgreSelectMapper.getMetadataById(id);
	}
	
	/* metadataId로 해당되는 Utterance 테이블을 가져옴 */
	@Override
	public List<Utterance> getUtteranceByMetadataId(Integer metadataId) {
		return postgreSelectMapper.getUtteranceByMetadataId(metadataId);
	}
	
	/* Metadata 테이블과 Program 테이블을 조인해서 가져옴 */
	@Override
	public List<Metadata> getMetadataAndProgram(){
		List<Metadata> list = sqlSession.selectList(ns+"getMetadataAndProgram");
		return list;
	}

	/* metadata id로 Metadata 테이블과 Program 테이블을 조인해서 가져옴 */
	@Override
	public Metadata getMetadataAndProgramUsingId(Integer metaId){
		Metadata list = sqlSession.selectOne(ns+"getMetadataAndProgramUsingId",metaId);
		return list;
	}
}