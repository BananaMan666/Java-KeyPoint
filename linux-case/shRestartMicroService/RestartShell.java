
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class RestartShell {
	private final static String DATA_SERVICE = "data-middle";
	private final static String MIDDLECOURT_SERVICE = "middlecourt";

	private final static String RESTART_MIDDLE_SERVICE_COMMAND = "nohup java -Xms1024m -Xmx5120m -XX:+HeapDumpOnOutOfMemoryError -jar planplat-middlecourt-biz.jar >logs/middlecourt.log 2>&1 &";
	private final static String RESTART_DATA_SERVICE_COMMAND = "nohup java -Xms512m -Xmx5120m -XX:+HeapDumpOnOutOfMemoryError -jar planplat-data-middle-biz.jar >logs/data-middle.log 2>&1 &";

	private final static String SERVICE_PARAM_MIDDLE = "java -Xms1024m -Xmx5120m -XX:+HeapDumpOnOutOfMemoryError -jar planplat-middlecourt-biz.jar";
	private final static String SERVICE_SERVICE_DATA = "java -Xms512m -Xmx5120m -XX:+HeapDumpOnOutOfMemoryError -jar planplat-data-middle-biz.jar";
	/**
	 * 根据服务名称，获取该服务的进程ID
	 *
	 * @param serviceName 微服务名称
	 * @return
	 */
	public String getCloudServicePid(String serviceName) {

		BufferedReader reader = null;
		String pId = null;

		try {
			//执行ps -ef命令，显示所有进程
			String psStr = "ps -ef|grep " + serviceName;
			System.out.println(psStr);
			Process process = Runtime.getRuntime().exec(psStr);

			reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = null;
			while ((line = reader.readLine()) != null) {
				System.out.println("查找到对应线程" + line);
				String[] strs = line.split("\\s+");
				pId = strs[1];
				System.out.println("对应线程ID：" + strs[1]);
			}

			return pId;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	/**
	 * 根据进程ID杀掉正在执行中的进程
	 * @param Pid
	 */
	public void closeLinuxProcess(String Pid) {
		Process process = null;
		BufferedReader reader = null;
		try {
			// 杀掉进程
			System.out.println("准备执行 kill -9 " + Pid);
			process = Runtime.getRuntime().exec("kill -9 " + Pid);
			reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = null;
			while ((line = reader.readLine()) != null) {
				System.out.println("kill PID return info -----> " + line);
			}
			//取消进程之后在结束该方法
			process.waitFor();
			System.out.println("关闭进程成功");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (process != null) {
				process.destroy();
			}

			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {

				}
			}
		}
	}

	public Boolean closeServiceByParam(String processParam){
		BufferedReader reader = null;

		try {
			//执行ps -ef命令，显示所有进程
			Process process = Runtime.getRuntime().exec("ps -ef");
			Process killProcess = null;

			reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = null;

			boolean exist = false;
			boolean kFlag = false;
			while ((line = reader.readLine()) != null) {
				if (line.contains(processParam)){
					System.out.println("查找到对应线程" + line);
					String[] strs = line.split("\\s+");
					if(null != strs[1]){
						exist = true;
						killProcess = Runtime.getRuntime().exec("kill -9 " + strs[1]);
						killProcess.waitFor();
						System.out.println("杀死服务进程，对应线程ID： " + strs[1]);
						kFlag = true;
					}
				}
			}

			//如果存在exist=true，则返回kFlag的状态（取消进程则返回true，取消失败则返回false）
			// 如果不存在，则直接返回true
			if(exist){
				return kFlag;
			}else {
				return Boolean.TRUE;
			}


		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}


	public void restartCloudService(String restartCommand) {
		Process process = null;
		BufferedReader reader = null;

		try {
			//重启微服务
			System.out.println("准备执行 重启服务 ");
			process = Runtime.getRuntime().exec(restartCommand);

			reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = null;
			System.out.println("重启服务 return info -----> " );
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
				if(line.contains("Started PlanPlatDataMiddleApplication in")){
					break;
				}
			}
			//取消进程之后在结束该方法
			process.waitFor();
			System.out.println("服务启动完成" );
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (process != null) {
				process.destroy();
			}

			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {

				}
			}
		}
	}

	public static void main(String[] args) {
		//获取参数部分
		String type = "m";
		if(args.length>0){
			type = args[0];
		}

		String param = SERVICE_SERVICE_DATA;
		String command = RESTART_DATA_SERVICE_COMMAND;
		if(type.equals("m")){
			param = SERVICE_PARAM_MIDDLE;
			command = RESTART_MIDDLE_SERVICE_COMMAND;
		}

		RestartShell reEntity = new RestartShell();
		boolean dflag = reEntity.closeServiceByParam(param);
		if(!dflag){
			System.out.println("服务关闭失败");
			return;
		}

		reEntity.restartCloudService(command);
	}

}
