package com.vs.planplat.middlecourt.util;

import com.sun.jna.Platform;
import com.vs.planplat.middlecourt.config.CustomConfig;
import com.vs.planplat.middlecourt.config.ProcessBuilderParam;
import com.vs.planplat.middlecourt.service.sqlite.ITmpRegulateService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.util.*;

/**
 * 进程的某些方法：
 */
@Component
@AllArgsConstructor
@Slf4j
public class ProcessUtil {
    private final CustomConfig customConfig;
    public final ITmpRegulateService tmpRegulateService;

    /**
     * 执行命令行，等待执行完成
     *
     * @param directory
     * @param logpath
     * @param customEnv
     * @param commands
     * @return
     */
    public int process(String directory, String logpath, Map<String, String> customEnv, String... commands) {
        ProcessBuilder pb = new ProcessBuilder(commands);
        pb.directory(new File(directory));
        File logFile = new File(logpath);
        Map<String, String> env = pb.environment();
        env.putAll(customEnv);


        //redirectErrorStream 属性默认值为false，意思是子进程的标准输出和错误输出被发送给两个独立的流，这些流可以通过 Process.getInputStream() 和 Process.getErrorStream() 方法来访问。
        //如果将值设置为 true，标准错误将与标准输出合并。这使得关联错误消息和相应的输出变得更容易。在此情况下，合并的数据可从 Process.getInputStream() 返回的流读取，而从 Process.getErrorStream() 返回的流读取将直接到达文件尾。
        pb.redirectErrorStream(true);
        pb.redirectOutput(ProcessBuilder.Redirect.appendTo(logFile));

        int runningStatus = 0;
        String s = null;
        InputStream is = null;
        StringBuilder sb = new StringBuilder();
        try {
            Process ps = pb.start();
            is = ps.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            log.info("processParams:" + Arrays.asList(commands).toString());
            log.info("processResult:" + sb.toString());
            runningStatus = ps.waitFor();
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        return runningStatus;
    }


    /**
     * 执行命令行，不等待执行完成
     *
     * @param directory
     * @param logpath
     * @param customEnv
     * @param commands
     * @return
     */
    public int processWithoutWait(String directory, String logpath, Map<String, String> customEnv, String... commands) {
        ProcessBuilder pb = new ProcessBuilder(commands);
        pb.directory(new File(directory));
        File logFile = new File(logpath);
//        Map<String, String> env = pb.environment();
//        env.putAll(customEnv);


        //redirectErrorStream 属性默认值为false，意思是子进程的标准输出和错误输出被发送给两个独立的流，这些流可以通过 Process.getInputStream() 和 Process.getErrorStream() 方法来访问。
        //如果将值设置为 true，标准错误将与标准输出合并。这使得关联错误消息和相应的输出变得更容易。在此情况下，合并的数据可从 Process.getInputStream() 返回的流读取，而从 Process.getErrorStream() 返回的流读取将直接到达文件尾。
//        pb.redirectErrorStream(true);
//        pb.redirectOutput(ProcessBuilder.Redirect.appendTo(logFile));

        int runningStatus = 0;
        String s = null;
        //InputStream is = null;
        StringBuilder sb = new StringBuilder();
        try {
            Process ps = pb.start();
//            is = ps.getInputStream();
//            BufferedReader br = new BufferedReader(new InputStreamReader(is));
//            String line;
//            while ((line = br.readLine()) != null) {
//                sb.append(line);
//            }
//            log.info("input:" + line);
            //         runningStatus = ps.waitFor();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
        }
        return runningStatus;
    }


    /**
     * 执行生成二进制文件的方法
     *
     * @param dbPath     db文件路径
     * @param resultPath 要生成的文件存放路径
     * @return
     */
    public int processWriteData(String dbPath, String resultPath, Integer areaId) {
//        ProcessBuilderParam processBuilderParam = customConfig.getProcessBuilderParam();
//        String[] commands = {processBuilderParam.getWriteDataCommand(), customConfig.getBaseDir().getProject(), dbPath, resultPath, areaId.toString(), "RT"};
//        Map<String, String> env = new HashMap<>();
//        env.put("LD_LIBRARY_PATH", processBuilderParam.getLibs());
//        return process(processBuilderParam.getDirectory(), processBuilderParam.getLog(), env, commands);
        return processWriteData(dbPath, resultPath, areaId, "RT");
    }

    /**
     * 执行生成二进制文件的方法
     *
     * @param dbPath     db文件路径
     * @param resultPath 要生成的文件存放路径
     * @param type       类型 RT|SPM
     * @return
     */
    public int processWriteData(String dbPath, String resultPath, Integer areaId, String type) {
        ProcessBuilderParam processBuilderParam = customConfig.getProcessBuilderParam();
        String[] commands = {processBuilderParam.getWriteDataCommand(), customConfig.getBaseDir().getProject(), dbPath, resultPath, areaId.toString(), type};
        Map<String, String> env = new HashMap<>();
        env.put("LD_LIBRARY_PATH", processBuilderParam.getLibs());
        return process(processBuilderParam.getDirectory(), processBuilderParam.getLog(), env, commands);
    }

    /**
     * 执行MC生成二进制文件的方法
     *
     * @param dbPath     db文件路径
     * @param resultPath 要生成的文件存放路径
     * @param type       类型 RT|SPM
     * @return
     */
    public int processMCWriteData(String dbPath, String resultPath, Integer areaId, String type) {
        ProcessBuilderParam processBuilderParam = customConfig.getMcWriteDataParam();
        String[] commands = {processBuilderParam.getWriteDataCommand(), customConfig.getBaseDir().getProject(), dbPath, resultPath, areaId.toString(), type};
        Map<String, String> env = new HashMap<>();
        env.put("LD_LIBRARY_PATH", processBuilderParam.getLibs());
        return process(processBuilderParam.getDirectory(), processBuilderParam.getLog(), env, commands);
    }

    /**
     * 执行MIMO插值
     *
     * @param sourcePath beam文件所在路径
     * @param resultPath 要生成的文件存放路径
     * @param resultPath 天线名数组
     * @return
     */
    public int processMimoInsertValue(String sourcePath, String resultPath, List<String> antennaNames) {
        ProcessBuilderParam processBuilderParam = customConfig.getMimoInsertValueParam();
        List<String> commandList = new ArrayList<>(Arrays.asList(processBuilderParam.getWriteDataCommand(), sourcePath, resultPath));
        commandList.addAll(antennaNames);
        String[] commands = new String[commandList.size()];
        commandList.toArray(commands);
        Map<String, String> env = new HashMap<>();
        //env.put("LD_LIBRARY_PATH", processBuilderParam.getLibs());
        return process(processBuilderParam.getDirectory(), processBuilderParam.getLog(), env, commands);
    }

    /**
     * 执行地图剖面图
     *
     * @param mapPath    地图zip路径
     * @param resultPath 要生成的文件存放路径
     * @param points     包含起点和重点，为平面坐标
     * @return
     */
    public int processMapPointInfo(String mapPath, String resultPath, List<Double[]> points) {
        ProcessBuilderParam processBuilderParam = customConfig.getMapPointInfoParam();
        String[] commands = {processBuilderParam.getWriteDataCommand(), points.get(0)[0].toString(), points.get(0)[1].toString(), points.get(1)[0].toString(), points.get(1)[1].toString(), mapPath, resultPath};
        Map<String, String> env = new HashMap<>();
        return process(processBuilderParam.getDirectory(), processBuilderParam.getLog(), env, commands);
    }

    /**
     * 执行python脚本执行工程
     *
     * @param paramJson JSON格式的参数
     * @return
     */
    public int processPython(String paramJson, String simType) {
        ProcessBuilderParam pythonCommand = customConfig.getPythonCommand();

        String[] commands = {pythonCommand.getWriteDataCommand(), paramJson, simType};
        Map<String, String> env = new HashMap<>();
        //  /usr/lib64/python2.7/site-packages
        env.put("PYTHONPATH", pythonCommand.getLibs());
        log.info("pythonCommand:" + pythonCommand.toString());
        return processWithoutWait(pythonCommand.getDirectory(), pythonCommand.getLog(), env, commands);
    }


	/**
	 * 执行spm传播模型校正算法，特别：需要添加lib文件
	 *
	 * @param directory 算法目录
	 * @param logPath 日志文件路径
	 * @param customEnv 环境参数
	 * @param historyId 仿真历史ID
	 * @param tmTargetPath 待生成spm传播模型路径
	 * @param commands 命令行
	 * @return
	 */
    @SneakyThrows
    public int processToSpm(String directory, String logPath, Map<String, String> customEnv, Integer historyId, String tmTargetPath, Integer projId,String type, String... commands) {
        ProcessBuilder pb = new ProcessBuilder(commands);
        pb.directory(new File(directory));
        File logFile = new File(logPath);
		/*Map<String, String> env = pb.environment();
		env.putAll(customEnv);*/


        //redirectErrorStream 属性默认值为false，意思是子进程的标准输出和错误输出被发送给两个独立的流，这些流可以通过 Process.getInputStream() 和 Process.getErrorStream() 方法来访问。
        //如果将值设置为 true，标准错误将与标准输出合并。这使得关联错误消息和相应的输出变得更容易。在此情况下，合并的数据可从 Process.getInputStream() 返回的流读取，而从 Process.getErrorStream() 返回的流读取将直接到达文件尾。
        pb.redirectErrorStream(true);
        pb.redirectOutput(ProcessBuilder.Redirect.appendTo(logFile));

        int runningStatus = 0;
        InputStream is = null;
        StringBuilder sb = new StringBuilder();
        try {
            Process ps = pb.start();
            Thread.sleep(100);
			String regulateProId = ProcessUtil.getProcessId(ps);
			ShService.printMessage("Thread.sleep(100); show regulateProId is " + regulateProId, logPath);

			//启动定时器，实现监督进程撤销与否
			RegulateKillTimer regulateKillTimer = new RegulateKillTimer(historyId, tmTargetPath, logPath, ps);
			regulateKillTimer.run();
			RegulateSchduleTimer regulateSchduleTimer = new RegulateSchduleTimer(projId, type, historyId, logPath);
			regulateSchduleTimer.run();

            is = ps.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            log.info("spmProcessResult:" + sb.toString());
            runningStatus = ps.waitFor();
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }

        return runningStatus;
    }

	/**
	 * 地理平均算法执行
	 *
	 * @param path 待处理文件
	 * @param frequency 频率
	 * @param speed	车速
	 * @param aveDistance 平均距离
	 * @param directory spm临时目录
	 * @return
	 */
	public Integer processToGeoAve(String path, Integer frequency, Integer speed, Integer aveDistance, String directory) {
		//调用算法
		String exePath = "/middleGround/v1/rtMedia/GeoAve";
		String[] commands = {exePath, path, String.valueOf(frequency), String.valueOf(speed), String.valueOf(aveDistance)};
		//demo:targetPath = /root/nfs/mg/projects/1/1/GD/366/transmodel/spm/tmp
		String geoAveLog = directory + File.separator + "geoAveLog.log";

		ProcessBuilder pb = new ProcessBuilder(commands);
		pb.directory(new File(directory));
		File logFile = new File(geoAveLog);
		//输出日志信息到log文件
		pb.redirectErrorStream(true);
		pb.redirectOutput(ProcessBuilder.Redirect.appendTo(logFile));

		int runningStatus = 0;
		InputStream is = null;
		StringBuilder sb = new StringBuilder();
		try {
			Process ps = pb.start();

			is = ps.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			log.info("geoAve:" + sb.toString());

			runningStatus = ps.waitFor();
		} catch (InterruptedException e) {
			log.error(e.getMessage(), e);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			try {
				is.close();
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}

		return runningStatus;
	}

    /**
     * 关闭Linux进程
     *
     * @param pid 进程的PID
     */
    public boolean killProcessByPid(String pid) {
        if (StringUtils.isEmpty(pid) || "-1".equals(pid)) {
            throw new RuntimeException("Pid ==" + pid);
        }
        Process process = null;
        BufferedReader reader = null;
        String command = "";
        boolean result = false;
        if (Platform.isWindows()) {
            command = "cmd.exe /c taskkill /PID " + pid + " /F /T ";
        } else if (Platform.isLinux() || Platform.isAIX()) {
            command = "kill -9 " + pid;
        }
        try {
            //杀掉进程
            process = Runtime.getRuntime().exec(command);
            reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "utf-8"));
            String line = null;
            while ((line = reader.readLine()) != null) {
                log.info("kill PID return info -----> " + line);
            }
            result = true;
        } catch (Exception e) {
            log.info("杀进程出错：", e);
            result = false;
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
        return result;
    }

    //获取当前服务的ip
    private String getServerIp() {
        try {
            //用 getLocalHost() 方法创建的InetAddress的对象
            InetAddress address = InetAddress.getLocalHost();
            return address.getHostAddress();
        } catch (Exception e) {
            return null;
        }
    }

    //获取当前进程ID
    private String getProcessId() {
        try {
            String name = ManagementFactory.getRuntimeMXBean().getName();

            // get pid
            String pid = name.split("@")[0];
            return pid;
        } catch (Exception e) {
            return null;
        }
    }

//    private int getPid(Process process) {
//        List<String> matches = RegexUtils.getMatches("pid=\\d*", process.toString());
//        String pid = "";
//        if (matches != null || matches.size() > 0) {
//            pid = matches.get(0);
//        }
//        List<String> pidList = RegexUtils.getMatches("\\d+", pid);
//        if (pidList != null || pidList.size() > 0) {
//            pid = pidList.get(0);
//        } else {
//            pid = "-1";
//        }
//        return Integer.valueOf(pid);
//    }
//public static void main(String[] args) throws IOException {
//    String command="D:\\Program Files (x86)\\Tencent\\TIM\\Bin\\QQScLauncher.exe";
//    Process process =Runtime.getRuntime().exec(command);
//    System.out.println(process.toString());
//}
	/**
	 * AI传播模型矫正
	 */
	public String processToAI( String logPath, String... commands)  {
		Arrays.stream(commands).forEach(s -> log.info("## "+s));//日志打印命令
		ProcessBuilder pb = new ProcessBuilder(commands);
		File logFile = new File(logPath);


		//redirectErrorStream 属性默认值为false，意思是子进程的标准输出和错误输出被发送给两个独立的流，这些流可以通过 Process.getInputStream() 和 Process.getErrorStream() 方法来访问。
		//如果将值设置为 true，标准错误将与标准输出合并。这使得关联错误消息和相应的输出变得更容易。在此情况下，合并的数据可从 Process.getInputStream() 返回的流读取，而从 Process.getErrorStream() 返回的流读取将直接到达文件尾。
		pb.redirectErrorStream(true);
		pb.redirectOutput(ProcessBuilder.Redirect.appendTo(logFile));

		String  regulateProId="";

		try {
			pb.start();
			regulateProId = ProcessUtil.getPID(commands[0]+" "+commands[1]);

		} catch (InterruptedException e) {
			log.error(e.getMessage(), e);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return regulateProId;


	}


	public static String getProcessId(Process process) {
		long pid = -1;
		Field field = null;
		if (Platform.isWindows()) {
			try {
				field = process.getClass().getDeclaredField("handle");
				field.setAccessible(true);
				pid = Kernel32.INSTANCE.GetProcessId((Long) field.get(process));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else if (Platform.isLinux() || Platform.isAIX()) {
			try {
				Class<?> clazz = Class.forName("java.lang.UNIXProcess");
				field = clazz.getDeclaredField("pid");
				field.setAccessible(true);
				pid = (Integer) field.get(process);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		return String.valueOf(pid);
	}
	/**
	 * 返回进程ID
	 * @param command
	 * @return
	 */
	public static String getPID(String command) throws Exception {

		BufferedReader reader = null;
		try {
			// 显示所有进程
			Process process = Runtime.getRuntime().exec("ps -ef");
			reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = null;
			while ((line = reader.readLine()) != null) {
				if (line.contains(command)) {
					System.out.println("查找到对应线程"+line);
					String[] strs = line.split("\\s+");
					return strs[1];
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {

				}
			}
		}
		log.info("找不到有关进程pid返回null");
		return null;
	}

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
	 * 执行仿真路测对比
	 * @param simResultPath
	 * @param roadtestPath
	 * @param resultPath
	 */
	public int processSimRoadtestComparison(String simResultPath, String roadtestPath, String resultPath) {
		ProcessBuilderParam processBuilderParam = customConfig.getSimRoadtestComparisonParam();
		List<String> commandList = new ArrayList<>(Arrays.asList(processBuilderParam.getWriteDataCommand(), simResultPath, roadtestPath,resultPath));
		String[] commands = new String[commandList.size()];
		commandList.toArray(commands);
		Map<String, String> env = new HashMap<>();
		//env.put("LD_LIBRARY_PATH", processBuilderParam.getLibs());
		return process(processBuilderParam.getDirectory(), processBuilderParam.getLog(), env, commands);
	}
}
