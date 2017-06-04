package cn.e3mall.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import cn.e3mall.common.utils.FastDFSClient;
import cn.e3mall.common.utils.JsonUtils;
/**
 * 图片  controller
 * @author Administrator
 *
 */
@Controller
public class PictureController {

	@Value("${IMAGE_SERVER_URL}")
	private String IMAGE_SERVER_URL;
	//指定响应结果的content-type
	@RequestMapping(value="/pic/upload",produces=MediaType.TEXT_PLAIN_VALUE+";charset=utf-8")
	@ResponseBody
	public String uploadPicture(MultipartFile uploadFile){
		Map result = new HashMap<>();
		try {
			//把图片上传到图片服务器
			FastDFSClient fastDFSClient = new FastDFSClient("classpath:conf/client.conf");
			//取文件的扩展名
			String originalFilename = uploadFile.getOriginalFilename();
			String extName = originalFilename.substring(originalFilename.lastIndexOf(",")+1);
			//得到一个图片的地址和文件名
			String url = fastDFSClient.uploadFile(uploadFile.getBytes(), extName);
			//补充为完整的URL
			url=IMAGE_SERVER_URL+url;
			//封装到map中返回
			result.put("error", 0);
			result.put("url", url);
		} catch (Exception e) {
			result.put("error", 1);
			result.put("message", "未知异常");
			e.printStackTrace();
		}
		//为了解决富文本编辑器 对浏览器兼容处理不好的情况 应该返回  text/plain类型的数据   
		//返回字符串  默认响应数据的类型就是text/plain的数据
		return JsonUtils.objectToJson(result);
	}
}
