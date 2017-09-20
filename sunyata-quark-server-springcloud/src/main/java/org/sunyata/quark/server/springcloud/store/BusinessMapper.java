/*
 *
 *
 *  * Copyright (c) 2017 Leo Lee(lichl.1980@163.com).
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 *  * use this file except in compliance with the License. You may obtain a copy
 *  * of the License at
 *  *
 *  *   http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  * License for the specific language governing permissions and limitations
 *  * under the License.
 *  *
 *
 */

package org.sunyata.quark.server.springcloud.store;

import org.apache.ibatis.annotations.*;
import org.sunyata.quark.store.BusinessComponentInstance;
import org.sunyata.quark.store.QuarkComponentInstance;
import org.sunyata.quark.store.QuarkComponentLog;
import org.sunyata.quark.store.QuarkParameter;

import java.sql.Timestamp;
import java.util.List;

@Mapper
public interface BusinessMapper {
    @Insert("INSERT INTO BusinessComponent(serialNo, businName,businFriendlyName,version," +
            "businStatus,canContinue,createDateTime,updateDateTime,needToRetry,businessMode,sponsor,relationId) " +
            "VALUES(#{serialNo}, #{businName},#{businFriendlyName},#{version},#{businStatus}," +
            "#{canContinue},#{createDateTime},#{updateDateTime},#{needToRetry},#{businessMode},#{sponsor}," +
            "#{relationId})")
    int insertByBusinessComponent(BusinessComponentInstance businessComponent);


    @Insert("INSERT INTO QuarkParameter(businessSerialNo, parameterType,parameter) " +
            "VALUES(#{businessSerialNo}, #{parameterType},#{parameter})")
    void insertByQuarkParameter(QuarkParameter quarkParameter);

    @Insert("INSERT INTO QuarkComponent(serialNo, businSerialNo,quarkName,quarkFriendlyName,targetQuarkName,version," +
            "createDateTime, orderby, subOrder, processResult, continueType) " +
            "VALUES(#{serialNo}, #{businSerialNo},#{quarkName},#{quarkFriendlyName},#{targetQuarkName},#{version}," +
            "#{createDateTime}," +
            "#{orderby},#{subOrder},#{processResult},#{continueType})")
    int insertByQuarkComponent(QuarkComponentInstance quarkComponent);

    @Update("UPDATE BusinessComponent SET businStatus=#{businStatus},notes=#{notes}," +
            "businessMode=#{businessMode},needToRetry=${needToRetry},canContinue=#{canContinue}, " +
            "updateDateTime=#{updateDateTime},priority=#{priority} WHERE " +
            "serialNo=#{serialNo}")
    void updateBusinessComponent(BusinessComponentInstance businessComponentInstance);

    @Update("UPDATE QuarkComponent SET processResult=#{processResult},notes=#{notes}," +
            "executeTimes=#{executeTimes} WHERE serialNo=#{serialNo}")
    void updateQuarkComponent(QuarkComponentInstance businessComponentInstance);

    //    int insertByUser(User user);
    @Select("SELECT * FROM BusinessComponent WHERE serialNo = #{serialNo}")
    BusinessComponentInstance findBySerialNo(@Param("serialNo") String serialNo);

    @Select("SELECT * FROM QuarkComponent WHERE businSerialNo = #{serialNo}")
    List<QuarkComponentInstance> findQuarkComponentInstances(@Param("serialNo") String serialNo);

    @Insert("INSERT INTO QuarkComponentLog(serialNo,businSerialNo,quarkName,quarkFriendlyName,version," +
            "createDateTime,processResult,notes,processResultString,totalMilliseconds,beginMilliseconds) " +
            "VALUES(#{serialNo}, #{businSerialNo},#{quarkName},#{quarkFriendlyName},#{version}," +
            "#{createDateTime}," +
            "#{processResult},#{notes},#{processResultString},#{totalMilliseconds},#{beginMilliseconds})")
    void insertByComponentLog(QuarkComponentLog quarkComponentLog);

    @Select("SELECT * FROM BusinessComponent WHERE TIMEDIFF(now(),updateDateTime) >'00:00:30' and  " +
            "businessMode = 'Normal' and needToRetry=1 and canContinue='CanContinue' order by priority  " +
            "asc, createDateTime asc")
    List<BusinessComponentInstance> findTopNWillRetryBusiness(Integer n);


    @Select("select * from BusinessComponent where TIMEDIFF(now(),createDateTime) >'00:10:00' and businStatus " +
            "= 'Initialize' order by updateDateTime ;")
    List<BusinessComponentInstance> findPastTenMinutesWillReBeginBusiness();


    @Select("SELECT * FROM QuarkParameter WHERE businessSerialNo = #{serialNo} and parameterType=#{parameterType}")
    QuarkParameter findQuarkParameter(@Param("serialNo") String serialNo, @Param("parameterType") int parameterType);

    @Update("UPDATE QuarkParameter SET parameter=#{parameter} WHERE businessSerialNo=#{businessSerialNo} and " +
            "parameterType=#{parameterType}")
    void updateQuarkParameter(QuarkParameter quarkParameterContext);

    @Update("UPDATE BusinessComponent SET updateDateTime=#{updateDateTime} WHERE serialNo=#{serialNo}")
    void updateBusinessComponentUpdateDateTime(@Param("serialNo") String serialNo, @Param("updateDateTime") Timestamp
            updateDateTime);

    @Select("SELECT id FROM BusinessComponent WHERE businName = #{businName} and relationId = #{relationId}")
    String findByBusinNameAndRelationId(BusinessComponentInstance instance);


//
//    @Insert("INSERT INTO USER(NAME, AGE) VALUES(#{name}, #{age})")
//    int insert(@Param("name") String name, @Param("age") Integer age);
//
//    @Insert("INSERT INTO USER(NAME, AGE) VALUES(#{name,jdbcType=VARCHAR}, #{age,jdbcType=INTEGER})")
//    int insertByMap(Map<String, Object> map);
//
//    @Insert("INSERT INTO USER(NAME, AGE) VALUES(#{name}, #{age})")
//    int insertByUser(User user);
//
//    @Update("UPDATE user SET age=#{age} WHERE name=#{name}")
//    void syncBusinessStatus(User user);
//    @Delete("DELETE FROM user WHERE id =#{id}")
//    void delete(Long id);
//
//    @Results({
//            @Result(property = "name", column = "name"),
//            @Result(property = "age", column = "age")
//    })
//    @Select("SELECT name, age FROM user")
//    List<User> findAll();
//

}
