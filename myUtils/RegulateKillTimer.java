package com.vs.planplat.middlecourt.util;

import com.vs.planplat.middlecourt.entity.base.ProjHistory;
import com.vs.planplat.middlecourt.enums.ProjectStatus;
import com.vs.planplat.middlecourt.service.base.ProjHistoryService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 定时任务：查看校正是否取消，从而杀掉正在执行的校正算法进程
 * 进程启动逻辑：
 * 	controller调用ShService中的invoke方法，
 * 	invoke方法调用linux本地的一个java jnaTestQ方法，该Q方法启动art算法进程
 * 	art算法在执行过程中，会启动四个进程，加快计算校正数据。
 * 	最后以上进程全部执行完成，校正结束。
 */
public class RegulateKillTimer extends TimerTask {

	//调用算法启动脚本路径
	private static final String STARTALGORITHM = "/middleGround/v1/rtMedia/testRun1/transmodel.sh";
	//校正执行算法路径
	private static final String ALGORITHMPATH = "/middleGround/v1/rtMedia/RTCalibrate//RTCalibrate";

	@Autowired
	public ProjHistoryService projHistoryService;

	private Integer projId;
	private Integer historyId;
	private String tmTargetPath;
	private String scheduleLogPath;
	private Process process;
	//嵌套进程的cmdline第二个参数
	private String dynamicParam;


	public RegulateKillTimer(Integer projId) {
		super();
		//自己需要含参构造且注入service
		projHistoryService = SpringContextUtil.getBean("projHistoryServiceImpl");
		this.projId = projId;
	}

	public RegulateKillTimer(Integer historyId, String tmTargetPath, String scheduleLogPath, Process process) {
		super();
		projHistoryService = SpringContextUtil.getBean("projHistoryServiceImpl");
		this.tmTargetPath = tmTargetPath;
		this.scheduleLogPath = scheduleLogPath;
		this.historyId = historyId;
		this.process = process;
	}

	public RegulateKillTimer(Integer historyId, String tmTargetPath, String scheduleLogPath, Process process, String dynamicParam) {
		super();
		projHistoryService = SpringContextUtil.getBean("projHistoryServiceImpl");
		this.tmTargetPath = tmTargetPath;
		this.scheduleLogPath = scheduleLogPath;
		this.historyId = historyId;
		this.process = process;
		this.dynamicParam = dynamicParam;
	}

	public RegulateKillTimer(){
		super();
	}


	@Override
	public void run() {
		new Timer().schedule(new TimerTask() {
			@SneakyThrows
			@Override
			public void run() {
				//这个判断是用老方法@Autowired注入的时候 报空指针 测试的时候在这儿判断了一下 是因为service空 没有成功注入 所有service/dao注入需要SpringContextUtil.getBean才可以
				if (projHistoryService == null) {
					ShService.printMessage("projHistoryService is null", scheduleLogPath);
				}
				System.out.println("kkkkkkkkk");

				ProjHistory projHistory = projHistoryService.getById(historyId);
				if(projHistory.getSimOperation().contains("ART") || projHistory.getSimOperation().contains("SPM")){
					//如果执行取消功能
					if(ProjectStatus.CANCEL.getStatus().equals(projHistory.getState())){
						//记录超级父进程
						String killProId = ProcessUtil.getProcessId(process);
						ShService.printMessage("kill processId " + killProId + " historyId：" + historyId + "校正状态：" + projHistory.getState(),
								scheduleLogPath);

						//杀掉进程 & 取消定时任务
						killRegulate(projHistory, dynamicParam);
						this.cancel();
					}

					//判断无效状态：如果一次仿真执行超过30分钟（状态为false），则认为该仿真出现问题
					/*if(!projHistoryService.handleInvalid(projHistory)){
						projHistory.setState(ProjectStatus.ERROR.getStatus());
						projHistory.setEndTime(LocalDateTime.now());
						projHistory.setSimSchedule("100");
						projHistoryService.updateById(projHistory);
						//杀掉进程 & 取消定时任务
						killRegulate(projHistory, dynamicParam);
						this.cancel();
					}*/

					//当校正完成（成功或者错误）时，定时任务也要取消
					if(ProjectStatus.RUN_COMPLETE.getStatus().equals(projHistory.getState()) || ProjectStatus.ERROR.getStatus().equals(projHistory.getState())){
						this.cancel();
					}
				}


			}
		}, 0, 1000 * 10);
	}

	/**
	 * 杀掉art或者spm校正进程
	 * spm校正算法单一进程执行
	 * art校正算法：ShService方法启动线程thread1：启动执行算法脚本，脚本调动art算法，art算法进程另外开启四个进程加快计算
	 */
	private void killRegulate(ProjHistory projHistory, String dynamicParam) throws Exception {
		//spm校正算法单一进程执行
		if(projHistory.getSimOperation().contains("SPM")){
			if(process.isAlive()){
				process.destroy();
			}
		}else if(projHistory.getSimOperation().contains("ART")){
			killProjArtProc(dynamicParam);
		}
	}


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

}
