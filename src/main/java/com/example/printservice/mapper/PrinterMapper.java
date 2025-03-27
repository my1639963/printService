package online.fantao.tools.printservice.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import online.fantao.tools.printservice.entity.Printer;

@Mapper
public interface PrinterMapper {
    
    @Select("SELECT * FROM printer WHERE deleted = 0")
    List<Printer> selectList();
    
    @Select("SELECT * FROM printer WHERE id = #{id} AND deleted = 0")
    Printer selectById(Long id);
    
    @Insert("INSERT INTO printer (name, model, ip_address, port, status, last_online_time, create_time, update_time, deleted) " +
            "VALUES (#{name}, #{model}, #{ipAddress}, #{port}, #{status}, #{lastOnlineTime}, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0)")
    int insert(Printer printer);
    
    @Update("UPDATE printer SET name = #{name}, model = #{model}, ip_address = #{ipAddress}, " +
            "port = #{port}, status = #{status}, last_online_time = #{lastOnlineTime}, " +
            "update_time = CURRENT_TIMESTAMP WHERE id = #{id} AND deleted = 0")
    int update(Printer printer);
    
    @Update("UPDATE printer SET status = #{status}, update_time = CURRENT_TIMESTAMP " +
            "WHERE id = #{id} AND deleted = 0")
    int updateStatus(Long id, String status);
    
    @Update("UPDATE printer SET deleted = 1, update_time = CURRENT_TIMESTAMP WHERE id = #{id}")
    int deleteById(Long id);
} 