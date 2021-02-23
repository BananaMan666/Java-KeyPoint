package com.vs.planplat.middlecourt.util;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.management.ManagementFactory;
import java.time.LocalDateTime;

/**
*	1、执行sh脚本
*	2、获取当前进程ID
*	3、向某个文件打印某句话或者日志
*/
@Component
@Slf4j
@AllArgsConstructor
public class ShService {

	/**
	 * 使用Q.sh脚本
	 * @param paramsPath 全部参数txt文件路径
	 * @param scheduleLogPath 导出算法日志存储路径 & 定时任务打印日志
	 * @param tmTargetJson 待生成的传播模型文件
	 * @param historyId 仿真历史ID
	 * @return 0：执行成功，1：执行失败
	 */
	public Integer invokeShell(String paramsPath, String scheduleLogPath, String tmTargetJson, Integer historyId){

		//方法2 在单独的进程中执行指定命令和变量。
		//第一个变量是sh命令，第二个变量是需要执行的脚本路径，从第三个变量开始是我们要传到脚本里的参数。
		//run1.sh 需要添加编译命令 javac
		String shPath = "/middleGround/v1/rtMedia/testRun1/transmodel.sh";
		String runMethod = "sh";
		String[] path=new String[]{runMethod, shPath, paramsPath, scheduleLogPath};

		//获取进程cmdline动态参数（工程信息） dynamicStr = "/root/nfs/mg/projects/86/196/ZJ/1700/transmodel/"
		String dynamicStr = paramsPath.substring(0, paramsPath.indexOf("temp"));

		try{
			Runtime runtime = Runtime.getRuntime();
			Process pro = runtime.exec(path);
			String regulateProId = ProcessUtil.getProcessId(pro);

			Thread.sleep(100);
			printMessage("Thread.sleep(100); regulateProId is " + regulateProId, scheduleLogPath);

			//启动定时器，实现监督进程撤销与否，和实时更新进度
			RegulateKillTimer regulateKillTimer = new RegulateKillTimer(historyId, tmTargetJson, scheduleLogPath, pro, dynamicStr);
			regulateKillTimer.run();

			int status = pro.waitFor();

			if (status != 0) {
				printMessage("Failed to call shell's command，status：" + status, scheduleLogPath);
			}
			return status;
		}
		catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}

		return 1;
	}

	//获取当前进程ID
	public String getProcessId() {
		try {
			String name = ManagementFactory.getRuntimeMXBean().getName();

			// get pid
			String pid = name.split("@")[0];
			return pid;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 打印信息到某个目录下，不会覆盖
	 * @param message
	 * @param logPath
	 */
	public static void printMessage(String message, String logPath){
		FileOutputStream o = null;
		byte[] buff = new byte[]{};

		message = LocalDateTime.now() +" " + message + "\n";
		try{
			File file = new File(logPath);
			if(!file.exists()){
				file.createNewFile();
			}
			buff = message.getBytes();
			//true:不覆盖原有内容，默认false：覆盖原有内容
			o = new FileOutputStream(file,true);
			o.write(buff);
			o.flush();
			o.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
