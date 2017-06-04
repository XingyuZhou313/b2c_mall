package cn.e3mall.fast;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.junit.Test;

public class FastDemo {
	
	/**
	 * 使用fastDFS 上传文件测试
	 * @throws MyException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	@Test
	public void testFast() throws Exception{
		//创建配置文件，文件名任意。内容就是tracker服务器的地址  tracker_server=192.168.25.133.22122
		//使用全局对象加载配置文件F:/Git-E3mall/e3-manager-web/src/test/resources/client.conf
		ClientGlobal.init("F:/Git-E3mall/e3-manager-web/src/test/resources/client.conf");
		//创建一个TrackerClient对象
		TrackerClient trackerClient = new TrackerClient();
		//通过TrackerClient获得一个TrackerServer对象
		TrackerServer trackerServer = trackerClient.getConnection();
		//创建StorageServer的引用，可以是NULL
		StorageServer storageServer=null;
		//创建一个StorageClient  参数需要   TrackerServer和StorageServer  
		StorageClient storageClient = new StorageClient(trackerServer, storageServer);
		//使用StorageClient对象上传文件，返回文件的路径和文件名
		//local_filename要上传的文件  file_ext_name文件扩展名 meta_list
		String[] file = storageClient.upload_file("C:/Users/Administrator/Desktop/store_image/timg.jpg", "jpg", null);
		// 返回数组中包含  组名和文件路径
		for (String string : file) {
			System.out.println(string);
		}
//		group1
//		M00/00/00/wKgZhVkwwRSAZzxVAABF0UUa7gs584.jpg
	}
}
