# java代码实现linux环境杀死进程

**Method1**：使用java自带的process进程变量中自带的destroy()方法进行销毁进程，适用于进程变量可以被获取的情况。

```java
private Process process;
process.destroy();
```



**Method2**：根据进程启动的参数，与/proc/12345(以进程ID名称所创建的文件)中**cmdline**文件中的参数，从所有进程中筛选出你要kill的进程，拿到进程ID，执行进程杀死方法。

```java
/**
 * 根据进程cmdline中的参数 杀死指定进程
 * @Param STARTALGORITHM：启动算法脚本路径（固定）
 * @Param ALGORITHMPATH：art校正算法路径（固定）
 * @Param dynamicParam：某个工程动态校正路径参数（不同工程不同路径）
 */
private void killProjArtProc(String dynamicParam) throws Exception {

   if(dynamicParam != null){
      List<String> stringList = new ArrayList<>();
      stringList.add(STARTALGORITHM);
      stringList.add(ALGORITHMPATH);
      stringList.add(dynamicParam);
      List<String> artPIds = ProcessUtil.getArtPIds(stringList);

      for (String pId : artPIds) {
         ProcessUtil.closeLinuxProcess(pId);
      }
   }
}


/**
	 * 根据进程cmdline含有的参数，返回包含参数的进程ID集合
	 * @param commands 参数必须是两个字符串的才行
	 * @return
	 */
	public static List<String> getArtPIds(List<String> commands) throws Exception {

		BufferedReader reader = null;
		List<String> procIdList = new ArrayList<>();
		String startStr = commands.get(0);
		String algStr = commands.get(1);
		String proStr = commands.get(2);
		try {
			//执行ps -ef命令，显示所有进程
			Process process = Runtime.getRuntime().exec("ps -ef");

			reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = null;
			while ((line = reader.readLine()) != null) {
				if ((line.contains(startStr) || line.contains(algStr)) && line.contains(proStr)) {
					System.out.println("查找到对应线程" + line);
					String[] strs = line.split("\\s+");
					procIdList.add(strs[1]);
					System.out.println("对应线程ID：" + strs[1]);
				}
			}

			return procIdList;
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
	 * 根据进程ID，关闭系统进程
	 * @param commands 参数必须是两个字符串的才行
	 * @return
	 */
public static void closeLinuxProcess(String Pid) {
		Process process = null;
		BufferedReader reader = null;
		try {
			// 杀掉进程
			log.info("准备执行 kill -9 " + Pid);
			process = Runtime.getRuntime().exec("kill -9 " + Pid);
			reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = null;
			while ((line = reader.readLine()) != null) {
				log.info("kill PID return info -----> " + line);
			}
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
```

