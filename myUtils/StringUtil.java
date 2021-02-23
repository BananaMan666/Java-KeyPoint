package com.vs.planplat.middlecourt.util;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimeUtility;
import com.vs.planplat.common.security.service.PlanplatUser;
import com.vs.planplat.middlecourt.entity.base.OutUserAk;
import com.vs.planplat.middlecourt.entity.sqlite.*;
import com.vs.planplat.middlecourt.vo.NodebVO;
import com.vs.planplat.middlecourt.vo.TransModelVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author xmm
 * @date 2020/4/13 19:33
 */
@Slf4j
public class StringUtil {

    /**
     * 包含以空格、回车符等字符开头 或者 空格、回车符等字符结尾 的字符串，可过滤出所有空格、回车符的字符
     */
    private final static Pattern BLANK_ENTER = Pattern.compile("(^\\s*)|(\\s*$)");



    /**
     * 中文文件名乱码处理方式
     *
     * @param request  请求
     * @param fileName 文件名
     * @return
     */
    public static String encodeFileName(HttpServletRequest request, String fileName) {
        String userAgent = request.getHeader("User-Agent");
        String rtn = "";
        try {
            String newFilename = URLEncoder.encode(fileName, "UTF8");
            // 如果没有UA，则默认使用IE的方式进行编码，因为毕竟IE还是占多数的
            rtn = "filename=\"" + newFilename + "\"";
            if (userAgent != null) {
                userAgent = userAgent.toLowerCase();
                // IE浏览器，只能采用URLEncoder编码
                if (userAgent.contains("msie")) {
                    rtn = "filename=\"" + newFilename + "\"";
                }
                // Opera浏览器只能采用filename*
                else if (userAgent.contains("opera")) {
                    rtn = "filename*=UTF-8''" + newFilename;
                }
                // Safari浏览器，只能采用ISO编码的中文输出
                else if (userAgent.contains("safari")) {
                    rtn = "filename=\"" + new String(fileName.getBytes(StandardCharsets.UTF_8), "ISO8859-1") + "\"";
                }
                // Chrome浏览器，只能采用MimeUtility编码或ISO编码的中文输出
                else if (userAgent.contains("applewebkit")) {
                    newFilename = MimeUtility.encodeText(fileName, "UTF8", "B");
                    rtn = "filename=\"" + newFilename + "\"";
                }
                // FireFox浏览器，可以使用MimeUtility或filename*或ISO编码的中文输出
                else if (userAgent.contains("mozilla")) {
                    //rtn = "filename*=UTF-8''" + new_filename;
                    newFilename = MimeUtility.encodeText(fileName, "UTF8", "B");
                    rtn = "filename=\"" + newFilename + "\"";
                }
            }
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
        }
        return rtn;
    }


    /**
     * 移除回车，空白字符
     *
     * @param str 字符串
     * @return 字符串
     */
    public static String getStringNoBlank(String str) {
        if (str != null && !"".equals(str)) {
            str = str.replaceAll("\n", "");
            Matcher m = BLANK_ENTER.matcher(str);
            return m.replaceAll("");
        } else {
            return "";
        }
    }
    public static String newLine(){
    	String lineSeparator;
        if (System.getProperty("os.name").toLowerCase().contains("win") == true) {
    	    lineSeparator = "\r\n";
        } else {
    	    lineSeparator = "\n";
        }
        return lineSeparator;
	}
    /**
     * 文件后缀名
     *
     * @param str 文件名
     * @return 后缀
     */
    public static String getFileExt(String str) {
        if (StrUtil.isEmpty(str)) {
            return "";
        }
        int index = StrUtil.lastIndexOfIgnoreCase(str, ".");
        if (index == -1) {
            return "";
        }
        return str.substring(index + 1);
    }

    public static LiteAntenna antennaeTxt(String path) {
        LiteAntenna antenna = new LiteAntenna();
        //读取index.txt文件
        File indexFile = new File(path + File.separator + "index.txt");
        FileReader fileReader = FileReader.create(indexFile, StandardCharsets.UTF_8);
        List<String> list = fileReader.readLines();

        if (CollectionUtil.isNotEmpty(list)) {
            List<LiteBeam> beamList1 = new ArrayList<>();
            List<LiteBeam> beamList2 = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                String str = list.get(i);
                if (StrUtil.isNotEmpty(str)) {
                    str = StrUtil.trim(str);
                    String[] arr = str.split("\\s+");
                    String name = arr[0];
                    String value = null;
                    if (arr.length >= 2) {
                        value = arr[1];
                        if ("ANTENNANAME".equals(name) && arr.length > 2) {
                            value = str.replace("ANTENNANAME ", "").trim();
                        }
						if ("MAKER".equals(name) && arr.length > 2) {
							value = str.replace("MAKER ", "").trim();
						}
                    }
                    if (StrUtil.isEmpty(value)) {
                        continue;
                    }
                    buildAntenna(antenna, name, value);

                    //非业务波束
                    if ("Broadcast_Beam_FILE".equals(name)) {
                        int count = Integer.parseInt(value);

                        List<String> bList = list.subList(i + 1, i + 1 + count);
                        i = i + count;
                        beam(path, beamList1, bList, "广播波束");
                    }
                    if ("Traffic_Beam_FILE".equals(name)) {
                        int count = Integer.parseInt(value);
                        List<String> tList = list.subList(i + 1, i + 1 + count);
                        i = i + count;
                        beam(path, beamList2, tList, "业务波束");
                        beamList1.addAll(beamList2);
                    }

                }
            }
            antenna.setServiceBeamNumber(beamList2.size());
            antenna.setBeams(beamList1);
        }
        return antenna;
    }

    /**
     * 根据文件构建天线信息，与index.txt进行字段对应
     *
     * @param antenna
     * @param name
     * @param value
     */
    private static void buildAntenna(LiteAntenna antenna, String name, String value) {
        if ("ANTENNANAME".equals(name)) {
            antenna.setAntennaName(value);
        }
        if ("MAKER".equals(name)) {
            antenna.setAntennaManufacturer(value);
        }
        if ("MAXFREQUENCY".equals(name)) {
            antenna.setMaxFrequency(Float.valueOf(value));
        }
        if ("MINFREQUENCY".equals(name)) {
            antenna.setMinFrequency(Float.valueOf(value));
        }
        if ("AntennaElementNumber".equals(name)) {
            antenna.setAntennaElementNumber(Integer.valueOf(value));
        }
        if ("Polarization".equals(name)) {
            antenna.setPolarization(PorlarizeEnum.getCode(value));
        }
        if ("H_WIDTH".equals(name)) {
            antenna.setHorizontalHalfPowerBeamWidth(Integer.valueOf(value));
        }
        if ("V_WIDTH".equals(name)) {
			Double parseDouble = Double.parseDouble(value);
			antenna.setVerticalHalfPowerBeamWidth(parseDouble.intValue());
        }
        if ("ElementGain".equals(name)) {
            antenna.setGainPerElement(Float.valueOf(value));
        }
        if ("ElectricalDownTilt".equals(name)) {
            antenna.setElectricalDownTilt(Integer.valueOf(value));
        }
    }


    private static void beam(String path, List<LiteBeam> beamList, List<String> bList, String beamType) {
        if (CollectionUtil.isNotEmpty(bList)) {
            for (String bStr : bList) {
                LiteBeam beam = new LiteBeam();
                bStr = StrUtil.trim(bStr);
                String[] bArr = bStr.split("\\s+");
                String bName = bArr[0];
                String min = null;
                String max = null;
                if (bArr.length >= 3) {
                    min = bArr[1];
                    max = bArr[2];
                }
                if (min == null) {
                    min = "0";
                }
                if (max == null) {
                    max = "359";
                }

                beam.setBeamName(getName(bName));
                beam.setUserBeamId(0);
                beam.setBeamType(beamType);
                beam.setMinAzimuth(Integer.parseInt(min));
                beam.setMaxAzimuth(Integer.parseInt(max));


                //解析文件
                File bFile = new File(path + File.separator + bName);
                FileReader bFileReader = FileReader.create(bFile, StandardCharsets.UTF_8);
                List<String> bLists = bFileReader.readLines();
                if (CollectionUtil.isNotEmpty(bLists)) {
                    List<LiteBeamHGain> hGainList = new ArrayList<>();
                    List<LiteBeamVGain> vGainList = new ArrayList<>();
                    int hCount = 0;
                    for (int bI = 0; bI < bLists.size(); bI++) {
                        String hStr = bLists.get(bI);
                        hStr = StrUtil.trim(hStr);
                        String[] hArr = hStr.split("\\s+");
                        if (hArr.length >= 2) {

                            String h1 = hArr[0];
                            String h2 = hArr[1];
                            if ("HORIZONTAL".equals(h1)) {
                                hCount = Integer.parseInt(h2);
                                continue;
                            }
                            if ("VERTICAL".equals(h1)) {
                                continue;
                            }
                            if (bI <= hCount) {
                                LiteBeamHGain hGain = new LiteBeamHGain();
                                hGain.setHorizontalAngel(Integer.parseInt(h1));
                                hGain.setHorizontalGain(Double.parseDouble(h2));
                                hGainList.add(hGain);
                            } else {
                                LiteBeamVGain vGain = new LiteBeamVGain();
                                vGain.setVerticalAngel(Integer.parseInt(h1));
                                vGain.setVerticalGain(Double.parseDouble(h2));
                                vGainList.add(vGain);
                            }

                        }
                    }
                    beam.setHGains(hGainList);
                    beam.setVGains(vGainList);
                    beamList.add(beam);
                }
            }
        }
    }

    /**
     * 根据文件名称返回beanName
     *
     * @param fileName
     * @return
     */
    public static String getName(String fileName) {
        if (StrUtil.isEmpty(fileName)) {
            return "";
        }
        return fileName.substring(0, fileName.lastIndexOf('.'));
    }

    /**
     * @param file    文件
     * @param path    路径
     * @param user    用户
     * @param dirFlag 是否创建压缩目录
     * @return 文件
     */
    public static File createFile(MultipartFile file, String path, PlanplatUser user, boolean dirFlag) {
        String format = DateUtil.format(new Date(), DatePattern.PURE_DATETIME_MS_PATTERN)
                + UUID.fastUUID().toString(true);
        String dir = path + "/" + user.getTenantId() + "/" + user.getId() + "/";
        //创建目录
        FileUtil.mkdir(dir);
        String fileName = dir + format;
        //后缀名
        String fileExt = FileUtil.extName(file.getOriginalFilename());
        //创建解压目录
        if (dirFlag) {
            FileUtil.mkdir(fileName);
        }
        if (StrUtil.isNotEmpty(fileExt)) {
            fileName = dir + format + "." + fileExt;
        }
        return new File(fileName);
    }

    /**
     * @param file      文件
     * @param path      路径
     * @param outUserAk 认真信息
     * @param dirFlag   是否创建压缩目录
     * @return 文件
     */
    public static File createFile(MultipartFile file, String path, OutUserAk outUserAk, boolean dirFlag) {
        String format = DateUtil.format(new Date(), DatePattern.PURE_DATETIME_MS_PATTERN)
                + UUID.fastUUID().toString(true);
        String dir = path + "/" + outUserAk.getTenantId() + "/" + outUserAk.getId() + "/";
        //创建目录
        FileUtil.mkdir(dir);
        String fileName = dir + format;
        //后缀名
        String fileExt = FileUtil.extName(file.getOriginalFilename());
        //创建解压目录
        if (dirFlag) {
            FileUtil.mkdir(fileName);
        }
        if (StrUtil.isNotEmpty(fileExt)) {
            fileName = dir + format + "." + fileExt;
        }
        return new File(fileName);
    }

    /**
     * @param file    文件
     * @param path    路径
     * @param user    用户
     * @param dirFlag 是否创建压缩目录
     * @return 文件
     */
    public static File createTransModelFile(MultipartFile file, String path, PlanplatUser user, boolean dirFlag) {
        String format = DateUtil.format(new Date(), DatePattern.PURE_DATETIME_MS_PATTERN)
                + UUID.fastUUID().toString(true);
        //去掉路径中的租户ID和用户ID
        String dir = path + "/";
        //创建目录
        FileUtil.mkdir(dir);
        String fileName = dir + format;
        //后缀名
        String fileExt = FileUtil.extName(file.getOriginalFilename());
        //创建解压目录
        if (dirFlag) {
            FileUtil.mkdir(fileName);
        }
        if (StrUtil.isNotEmpty(fileExt)) {
            fileName = dir + format + "." + fileExt;
        }
        return new File(fileName);
    }

    public static String getRandomFileName() {
        return DateUtil.format(new Date(), DatePattern.PURE_DATETIME_MS_PATTERN)
                + UUID.fastUUID().toString(true);
    }

    public static File createDirAndFile(MultipartFile file, String path, PlanplatUser user) {
        String format = DateUtil.format(new Date(), DatePattern.PURE_DATETIME_MS_PATTERN)
                + UUID.fastUUID().toString(true);
        String dir = path + "/" + user.getTenantId() + "/" + user.getId() + "/" + format + "/";
        //创建目录
        FileUtil.mkdir(dir);
        String fileName = dir + format;
        //后缀名
        String fileExt = FileUtil.extName(file.getOriginalFilename());

        if (StrUtil.isNotEmpty(fileExt)) {
            fileName = dir + format + "." + fileExt;
        }
        return new File(fileName);
    }

	/**
	 * 生成AI仿真参数文件
	 * @param antennaName
	 * @param h
	 * @param v
	 * @param path
	 * @return
	 */
    public static File antennaExportForAIRegulateParams(String antennaName,List<LiteBeamHGain> h,
														List<LiteBeamVGain> v,String path){
    	if (antennaName == "" && h.isEmpty() && v.isEmpty()){
    		return null;
		}
    	File index = new File(path +File.separator + antennaName +".txt");
    	FileWriter fw = null;
		BufferedWriter out = null;
		try{
			index.createNewFile();
			fw = new FileWriter(index);
			out = new BufferedWriter(fw);
			out.write(new String(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF}));
			out.write("HORIZONTAL"+"\t"+h.size());
			out.newLine();
			int i =0;
			for (LiteBeamHGain hor : h){
				out.write( ""+i + "\t" + hor.getHorizontalGain());
				out.newLine();
			    i++;
			}
			out.write("VERTICAL"+"\t"+v.size());
			out.newLine();
			i=0;
			for (LiteBeamVGain ver: v){
                out.write(""+i +"\t" + ver.getVerticalGain());
                out.newLine();
                i++;
			}
			out.flush();
			return index;
		}catch (IOException e){
			log.error(e.getMessage(), e);
		}finally {
			if (out != null) {
				IoUtil.closeIfPosible(out);
				IoUtil.closeIfPosible(fw);
			}
		}
    	return null;

	}
    /**
     * 导出天线
     *
     * @param antenna
     * @return
     */
    public static File antennaExport(LiteAntenna antenna, String path) {
        if (antenna == null) {
            return null;
        }
        String antennaName = antenna.getAntennaName();
        String dir = path + File.separator + antennaName;
        //创建目录
        FileUtil.mkdir(dir);
        //创建index.txt
        File index = new File(dir + File.separator + "index.txt");
        FileWriter fw = null;
        BufferedWriter out = null;
        try {
            index.createNewFile();
            fw = new FileWriter(index);
            out = new BufferedWriter(fw);
            out.write(new String(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF}));
            out.write("ANTENNANAME " + antennaName);
            out.newLine();
            out.write("MAKER " + antenna.getAntennaManufacturer());
            out.newLine();
            out.write("MAXFREQUENCY " + antenna.getMaxFrequency().intValue());
            out.newLine();
            out.write("MINFREQUENCY " + antenna.getMinFrequency().intValue());
            out.newLine();
            out.write("AntennaElementNumber " + antenna.getAntennaElementNumber());
            out.newLine();
            out.write("Polarization " + PorlarizeEnum.getValue(antenna.getPolarization()));
            out.newLine();
            out.write("H_WIDTH " + antenna.getHorizontalHalfPowerBeamWidth());
            out.newLine();
            out.write("V_WIDTH " + antenna.getVerticalHalfPowerBeamWidth());
            out.newLine();
            out.write("ElementGain " + antenna.getGainPerElement().intValue());
            out.newLine();
            out.write("ElectricalDownTilt " + antenna.getElectricalDownTilt().intValue());
            out.newLine();
            List<LiteBeam> beamList = antenna.getBeams();
            List<LiteBeam> beamList1 = beamList.stream().filter(t -> "广播波束".equals(t.getBeamType())).collect(Collectors.toList());
            List<LiteBeam> beamList2 = beamList.stream().filter(t -> "业务波束".equals(t.getBeamType())).collect(Collectors.toList());
            out.write("Broadcast_Beam_FILE " + beamList1.size());
            out.newLine();
            for (LiteBeam beam : beamList1) {
                out.write(beam.getBeamName() + ".txt " + beam.getMinAzimuth() + " " + beam.getMaxAzimuth());
                out.newLine();
            }
            out.write("Traffic_Beam_FILE " + beamList2.size());
            out.newLine();
            for (LiteBeam beam : beamList2) {
                out.write(beam.getBeamName() + ".txt " + beam.getMinAzimuth() + " " + beam.getMaxAzimuth());
                out.newLine();
            }
            out.flush();
            //生成波束文件
            beamFile(beamList, dir);
            return ZipUtil.zip(dir);
        } catch (IOException e) {
            log.error(e.getMessage(), e);

        } finally {
            if (out != null) {
                IoUtil.closeIfPosible(out);
                IoUtil.closeIfPosible(fw);
            }
        }
        return null;
    }

    private static void beamFile(List<LiteBeam> list, String dir) {
        if (CollectionUtil.isEmpty(list)) {
            return;
        }
        for (LiteBeam beam : list) {
            String name = beam.getBeamName();
            String path = dir + File.separator + name + ".txt";
            List<LiteBeamHGain> beamHGainList = beam.getHGains();
            List<LiteBeamVGain> beamVGainList = beam.getVGains();
            File index = new File(path);
            FileWriter fw = null;
            BufferedWriter out = null;
            try {
                index.createNewFile();
                fw = new FileWriter(index);
                out = new BufferedWriter(fw);
                out.write(new String(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF}));
                out.write("HORIZONTAL" + "\t" + beamHGainList.size());
                out.newLine();
                for (LiteBeamHGain hGain : beamHGainList) {
                    out.write(hGain.getHorizontalAngel() + "\t" + hGain.getHorizontalGain());
                    out.newLine();
                }
                out.write("VERTICAL" + "\t" + beamVGainList.size());
                out.newLine();
                for (LiteBeamVGain beamVGain : beamVGainList) {
                    out.write(beamVGain.getVerticalAngel() + "\t" + beamVGain.getVerticalGain());
                    out.newLine();
                }
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            } finally {
                IoUtil.closeIfPosible(out);
                IoUtil.closeIfPosible(fw);
            }
        }
    }


    /**
     * jsonObject 转化为传播模型详情
     *
     * @param jo json对象
     * @return 传播模型
     */
    public static List<TransModelVo> jsonToTransModel(JSONObject jo) {
        JSONArray materials = jo.getJSONArray("materials");
        if (CollectionUtil.isNotEmpty(materials)) {
            List<TransModelVo> list = new ArrayList<>();
            materials.forEach(m -> {
                JSONObject mt = (JSONObject) m;
                TransModelVo vo = new TransModelVo();
                String name = mt.getStr("name");
                vo.setName(name);
                JSONArray realPermittivity = mt.getJSONArray("RealPermittivity");
                if (CollectionUtil.isNotEmpty(realPermittivity)) {
                    vo.setRealPermittivity(realPermittivity.getDouble(0));
                }
                JSONArray lossTangent = mt.getJSONArray("LossTangent");
                if (CollectionUtil.isNotEmpty(lossTangent)) {
                    vo.setLossTangent(lossTangent.getDouble(0));
                }
                JSONObject transmission = mt.getJSONObject("transmission");
                JSONArray transmissionLoss = transmission.getJSONArray("loss");
                if (CollectionUtil.isNotEmpty(transmissionLoss)) {
                    vo.setO2oLoss(transmissionLoss.getDouble(0));
                }
                JSONArray transmissionCoeff = transmission.getJSONArray("coeff");
                if (CollectionUtil.isNotEmpty(transmissionCoeff)) {
                    vo.setO2oCoeff(transmissionCoeff.getDouble(0));
                }
                JSONArray transmissionLossO2Iver = transmission.getJSONArray("LossO2Iver");
                if (CollectionUtil.isNotEmpty(transmissionLossO2Iver)) {
                    vo.setLossO2Iver(transmissionLossO2Iver.getDouble(0));
                }
                JSONArray transmissionLossO2Ihor = transmission.getJSONArray("LossO2Ihor");
                if (CollectionUtil.isNotEmpty(transmissionLossO2Ihor)) {
                    vo.setLossO2Ihor(transmissionLossO2Ihor.getDouble(0));
                }
                JSONArray transmissionLossO2I = transmission.getJSONArray("LossO2I");
                if (CollectionUtil.isNotEmpty(transmissionLossO2I)) {
                    vo.setLossO2I(transmissionLossO2I.getDouble(0));
                }
                JSONObject deygout = mt.getJSONObject("Deygout");
                JSONArray deygoutCoeff = deygout.getJSONArray("coeff");
                if (CollectionUtil.isNotEmpty(deygoutCoeff)) {
                    vo.setDeygoutCoeff(deygoutCoeff.getDouble(0));
                }
                JSONObject clutter = mt.getJSONObject("Clutter");
                JSONArray clutterLoss = clutter.getJSONArray("Loss");
                if (CollectionUtil.isNotEmpty(clutterLoss)) {
                    vo.setLoss(clutterLoss.getDouble(0));
                }
                JSONArray clutterSelfLoss = clutter.getJSONArray("SelfLoss");
                if (CollectionUtil.isNotEmpty(clutterSelfLoss)) {
                    vo.setSelfLoss(clutterSelfLoss.getDouble(0));
                }
                list.add(vo);
            });
            return list;
        }

        return null;
    }

    /**
     * 生成一个固定名字的文件
     *
     * @param file    文件
     * @param path    文件路径
     * @param user    用户
     * @param dirFlag
     * @param fixName 固定名字
     * @return
     */
    public static File createTransModelFileFixname(MultipartFile file, String path, PlanplatUser user, boolean dirFlag, String fixName) {
		/*String format = DateUtil.format(new Date(), DatePattern.PURE_DATETIME_MS_PATTERN)
				+ UUID.fastUUID().toString(true);*/
        //去掉路径中的租户ID和用户ID
        String dir = path + "/";
        //创建目录
        FileUtil.mkdir(dir);
        String fileName = dir + fixName;
        //后缀名
        String fileExt = FileUtil.extName(file.getOriginalFilename());
        //创建解压目录
        if (dirFlag) {
            FileUtil.mkdir(fileName);
        }
        if (StrUtil.isNotEmpty(fileExt)) {
            fileName = dir + fixName + "." + fileExt;
        }
        return new File(fileName);
    }

    public static LiteAntenna3d antennae3dTxt(String path) throws Exception {
        LiteAntenna3d antenna = new LiteAntenna3d();
        //读取index.txt文件
        File indexFile = new File(path + File.separator + "index.txt");
        FileReader fileReader = FileReader.create(indexFile, StandardCharsets.UTF_8);
        List<String> list = fileReader.readLines();

        if (CollectionUtil.isNotEmpty(list)) {
            List<LiteBeam3d> beamList1 = new ArrayList<>();
            List<LiteBeam3d> beamList2 = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                String str = list.get(i);
                if (StrUtil.isNotEmpty(str)) {
                    str = StrUtil.trim(str);
                    String[] arr = str.split("\\s+");
                    String name = arr[0];
                    String value = null;
                    if (arr.length >= 2) {
                        value = arr[1];
                        if ("ANTENNANAME".equals(name) && arr.length > 2) {
                            value = str.replace("ANTENNANAME ", "");
                        }
						if ("MAKER".equals(name) && arr.length > 2) {
							value = str.replace("MAKER ", "").trim();
						}
                    }
                    if (StrUtil.isEmpty(value)) {
                        continue;
                    }
                    buildAntenna3d(antenna, name, value);

                    //非业务波束
                    if ("Broadcast_Beam_FILE".equals(name)) {
                        int count = Integer.parseInt(value);

                        List<String> bList = list.subList(i + 1, i + 1 + count);
                        i = i + count;
                        beam3d(path, beamList1, bList, "广播波束");
                    }
                    if ("Traffic_Beam_FILE".equals(name)) {
                        int count = Integer.parseInt(value);
                        List<String> tList = list.subList(i + 1, i + 1 + count);
                        i = i + count;
                        beam3d(path, beamList2, tList, "业务波束");
                        beamList1.addAll(beamList2);
                    }

                }
            }
            antenna.setServiceBeamNumber(beamList2.size());
            antenna.setBeamList(beamList1);
        }
        return antenna;
    }

    private static void buildAntenna3d(LiteAntenna3d antenna, String name, String value) {
        if ("ANTENNANAME".equals(name)) {
            antenna.setAntennaName(value);
        }
        if ("MAKER".equals(name)) {
            antenna.setAntennaManufacturer(value);
        }
        if ("MAXFREQUENCY".equals(name)) {
            antenna.setMaxFrequency(Float.valueOf(value));
        }
        if ("MINFREQUENCY".equals(name)) {
            antenna.setMinFrequency(Float.valueOf(value));
        }
        if ("AntennaElementNumber".equals(name)) {
            antenna.setAntennaElementNumber(Integer.valueOf(value));
        }
        if ("Polarization".equals(name)) {
            antenna.setPolarization(PorlarizeEnum.getCode(value));
        }
        if ("H_WIDTH".equals(name)) {
            antenna.setHorizontalHalfPowerBeamWidth(Integer.valueOf(value));
        }
//        if ("V_WIDTH".equals(name)) {
//            antenna.setVerticalHalfPowerBeamWidth(Integer.valueOf(value));
//        }
		if ("V_WIDTH".equals(name)) {
			Double parseDouble = Double.parseDouble(value);
			antenna.setVerticalHalfPowerBeamWidth(parseDouble.intValue());
		}
        if ("ElementGain".equals(name)) {
            antenna.setGainPerElement(Float.valueOf(value));
        }
        if ("ElectricalDownTilt".equals(name)) {
            antenna.setElectricalDownTilt(Integer.valueOf(value));
        }
    }

    private static void beam3d(String path, List<LiteBeam3d> beamList, List<String> bList, String beamType) throws Exception {
        if (CollectionUtil.isNotEmpty(bList)) {
            for (String bStr : bList) {
                LiteBeam3d beam = new LiteBeam3d();
                bStr = StrUtil.trim(bStr);
                String[] bArr = bStr.split("\\s+");
                String bName = bArr[0];
                String min = null;
                String max = null;
                if (bArr.length >= 3) {
                    min = bArr[1];
                    max = bArr[2];
                }
                if (min == null) {
                    min = "0";
                }
                if (max == null) {
                    max = "359";
                }

                beam.setBeamName(getName(bName));
                beam.setMinAzimuth(Integer.parseInt(min));
                beam.setMaxAzimuth(Integer.parseInt(max));
                beam.setBeamType(beamType);
                beam.setUserBeamId(0);
                //解析文件
                File bFile = new File(path + File.separator + bName);
                FileReader bFileReader = FileReader.create(bFile, StandardCharsets.UTF_8);
                List<String> bLists = bFileReader.readLines();
                if (CollectionUtil.isNotEmpty(bLists)) {
                    List<LiteBeamGain3d> paraBeamGain3ds = new ArrayList<>();
                    for (int bI = 1; bI < bLists.size(); bI++) {
                        String hStr = bLists.get(bI);
                        hStr = StrUtil.trim(hStr);
                        String[] hArr = hStr.split("\\s+");
                        if (hArr.length > 2) {
                            String horizontalAngel = hArr[0];
                            String verticalAngel = hArr[1];
                            String gain = hArr[2];
                            LiteBeamGain3d paraBeamGain3d = new LiteBeamGain3d();
                            paraBeamGain3d.setHorizontalAngel(Integer.valueOf(horizontalAngel));
                            paraBeamGain3d.setVerticalAngel(Integer.valueOf(verticalAngel));
                            paraBeamGain3d.setGain(Float.valueOf(gain));
                            paraBeamGain3ds.add(paraBeamGain3d);
                        } else {
                            throw new Exception("文件格式不合法,每行应有3列：" + hStr);
                        }
                    }
                    beam.setLiteBeamGains(paraBeamGain3ds);
                    beamList.add(beam);
                }
            }
        }
    }

    /**
     * 导出三维天线
     *
     * @param antenna
     * @return
     */
    public static File antennaExport3d(LiteAntenna3d antenna, String path) {
        if (antenna == null) {
            return null;
        }
        String antennaName = antenna.getAntennaName();
        String dir = path + File.separator + antennaName;
        //创建目录
        FileUtil.mkdir(dir);
        //创建index.txt
        File index = new File(dir + File.separator + "index.txt");
        FileWriter fw = null;
        BufferedWriter out = null;
        try {
            index.createNewFile();
            fw = new FileWriter(index);
            out = new BufferedWriter(fw);
            out.write(new String(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF}));
            out.write("ANTENNANAME " + antennaName);
            out.newLine();
            out.write("MAKER " + antenna.getAntennaManufacturer());
            out.newLine();
            out.write("MAXFREQUENCY " + antenna.getMaxFrequency().intValue());
            out.newLine();
            out.write("MINFREQUENCY " + antenna.getMinFrequency().intValue());
            out.newLine();
            out.write("AntennaElementNumber " + antenna.getAntennaElementNumber());
            out.newLine();
            out.write("Polarization " + PorlarizeEnum.getValue(antenna.getPolarization()));
            out.newLine();
            out.write("H_WIDTH " + antenna.getHorizontalHalfPowerBeamWidth());
            out.newLine();
            out.write("V_WIDTH " + antenna.getVerticalHalfPowerBeamWidth());
            out.newLine();
            out.write("ElementGain " + antenna.getGainPerElement().intValue());
            out.newLine();
            out.write("ElectricalDownTilt " + antenna.getElectricalDownTilt().intValue());
            out.newLine();
            List<LiteBeam3d> beamList = antenna.getBeamList();
            List<LiteBeam3d> beamList1 = beamList.stream().filter(t -> "广播波束".equals(t.getBeamType())).collect(Collectors.toList());
            List<LiteBeam3d> beamList2 = beamList.stream().filter(t -> "业务波束".equals(t.getBeamType())).collect(Collectors.toList());
            out.write("Broadcast_Beam_FILE " + beamList1.size());
            out.newLine();
            for (LiteBeam3d beam : beamList1) {
                out.write(beam.getBeamName() + ".txt " + beam.getMinAzimuth() + " " + beam.getMaxAzimuth());
                out.newLine();
            }
            out.write("Traffic_Beam_FILE " + beamList2.size());
            out.newLine();
            for (LiteBeam3d beam : beamList2) {
                out.write(beam.getBeamName() + ".txt " + beam.getMinAzimuth() + " " + beam.getMaxAzimuth());
                out.newLine();
            }
            out.flush();
            //生成波束文件
            beamFile3d(beamList, dir);
            return ZipUtil.zip(dir);
        } catch (IOException e) {
            log.error(e.getMessage(), e);

        } finally {
            if (out != null) {
                IoUtil.closeIfPosible(out);
                IoUtil.closeIfPosible(fw);
            }
        }
        return null;
    }

    private static void beamFile3d(List<LiteBeam3d> list, String dir) {
        if (CollectionUtil.isEmpty(list)) {
            return;
        }
        for (LiteBeam3d beam : list) {
            String name = beam.getBeamName();
            String path = dir + File.separator + name + ".txt";
            List<LiteBeamGain3d> beamHGainList = beam.getLiteBeamGains();
            File index = new File(path);
            FileWriter fw = null;
            BufferedWriter out = null;
            try {
                index.createNewFile();
                fw = new FileWriter(index);
                out = new BufferedWriter(fw);
                out.write(new String(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF}));
                out.write("HORIZONTAL" + "\t" + "VERTICAL" + "\t" + beamHGainList.size());
                out.newLine();
                for (LiteBeamGain3d hGain : beamHGainList) {
                    out.write(hGain.getHorizontalAngel() + "\t" + hGain.getVerticalAngel() + "\t" + hGain.getGain());
                    out.newLine();
                }

            } catch (IOException e) {
                log.error(e.getMessage(), e);
            } finally {
                IoUtil.closeIfPosible(out);
                IoUtil.closeIfPosible(fw);
            }
        }
    }

	//去掉字符串中的中文
	public static String getLongLoadPermit1(String brandName) {
		String reg = "[\u4E00-\u9FA5]";
		Pattern pat = Pattern.compile(reg);
		Matcher mat = pat.matcher(brandName);
		String longLoadPermit = mat.replaceAll("");
		return longLoadPermit;
	}

	/**
	 * 获取txt文件中名字对应的尾部数据：适用于xxx yyy
	 *
	 * @param path txt文件路径
	 * @param headers 待要获取的字段头部分
	 * @return
	 */
	public static Map<String, String> getTxtLastField(String path, List<String> headers){
		File file = new File(path);
		if(!file.exists()){
			return null;
		}

		Map<String, String> map = new HashMap<String, String>();
		try {
			InputStream is = new FileInputStream(file);
			String line; // 用来保存每行读取的内容
			String str = "";
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			line = reader.readLine(); // 读取第一行

			while (line != null) { // 如果 line 为空说明读完了
				String header;
				String laster;
				//设置正则将多余空格都转为一个空格
				str = line + "\r\n";
				String[] dictionary = str.split("\\s{1,}|\t");

				header = dictionary[0];
				laster = dictionary[1];
				if(headers.contains(header)){
					map.put(header, laster);
				}

				line = reader.readLine(); // 读取下一行
			}
			reader.close();
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * 获取txt文件中所有的内容
	 *
	 * @param path 文件路径
	 * @return
	 */
	public static List<String> getTxtContent(String path){
		File file = new File(path);
		if(!file.exists()){
			return null;
		}

		List<String> list = new ArrayList<String>();
		try {
			InputStream is = new FileInputStream(file);
			String line; // 用来保存每行读取的内容
			String str = "";
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			line = reader.readLine(); // 读取第一行

			while (line != null) { // 如果 line 为空说明读完了
				str = line;
				list.add(str);

				line = reader.readLine(); // 读取下一行
			}
			reader.close();
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	//判断一个字符串是否是整数
	public static boolean isInteger(String str) {
		Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
		return pattern.matcher(str).matches();
	}

	// 判断一个字符串是否含有数字
	public static boolean HasDigit(String content) {
		boolean flag = false;
		Pattern p = Pattern.compile(".*\\d+.*");
		Matcher m = p.matcher(content);
		if (m.matches()) {
			flag = true;
		}
		return flag;
	}

	//将数组存储的nodebids转化成list
    public static List<Integer> paraseNodebArray(String nodebIds) {
		if(!HasDigit(nodebIds)){
			return null;
		}
		String tmp = nodebIds.substring(1, nodebIds.length()-1);
		String[] array = tmp.split(",");

		List<Integer> list = new ArrayList<>();
		for (String s : array) {
			list.add(Integer.parseInt(s.trim()));
		}
		return list;
    }

	/**
	 * 读取路测文件内容并返回 list
	 *
	 * @param cwfile
	 * @return List<NodebVO>
	 */
	public static List<NodebVO> readCWFile(String cwfile) {
		File cwFile = new File(cwfile);
		if (!cwFile.exists()) {
			System.out.println(cwFile + "Open Fail!"); //或者文件不存在
			return null;
		}
		List<NodebVO> list = new ArrayList<>();
		try {
			InputStream is = new FileInputStream(cwFile);
			String line; // 用来保存每行读取的内容
			String str = "";
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			reader.readLine();
			line = reader.readLine(); // 读取第一行

			while (line != null) { // 如果 line 为空说明读完了
				double x, y, RSRP;
				Integer id;
				//设置正则将多余空格都转为一个空格
				str = line + "\r\n";
				String[] dictionary = str.split("\\s{2,}|\t");

				x = Double.parseDouble(dictionary[0]);
				y = Double.parseDouble(dictionary[1]);
				RSRP = Double.parseDouble(dictionary[2]);
				id = Integer.parseInt(dictionary[3]);//cwfile 中的基站ID

				NodebVO nodebVO = new NodebVO();
				nodebVO.setLongitude(x);//经度坐标lo-x
				nodebVO.setLatitude(y);//纬度坐标la-y
				nodebVO.setRsrp(RSRP);
				nodebVO.setNodebId(id);
				list.add(nodebVO);

				line = reader.readLine(); // 读取下一行
			}
			reader.close();
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 合并多个txt文件到指定txt文件中去
	 *
	 * @param targetPath 目标路径
	 * @param sourcePathList 待合并资源路径
	 * @throws IOException
	 */
	public static boolean mergeManyTxt(String targetPath, List<String> sourcePathList) throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(targetPath));

		//记录第一个文件名称（会保留表头字段）
		String firstFileName = null;
		File[] list = new File[sourcePathList.size()];
		for (int i = 0; i < sourcePathList.size(); i++) {
			File file = new File(sourcePathList.get(i));
			if(!file.exists()){
				log.error("第" + i + "个文件不存在");
				return Boolean.FALSE;
			}
			list[i] = file;
			if(i == 0){
				firstFileName = file.getName();
			}
		}

		try {
			for(File file : list) {
				InputStream is = new FileInputStream(file);
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				String line; // 用来保存每行读取的内容

				if(!file.getName().equals(firstFileName)){
					//如果不是第一个文件，则空置第一条表头数据
					br.readLine();
				}
				while((line = br.readLine())!=null) {
					bw.write(line);
					bw.newLine();
				}
				br.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			bw.close();
		}

		return Boolean.TRUE;
	}
}
