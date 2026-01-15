package com.jingyao.jingyaoaicodeassistant.utils;

import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.jingyao.jingyaoaicodeassistant.exception.BusinessException;
import com.jingyao.jingyaoaicodeassistant.exception.ErrorCode;
import io.github.bonigarcia.wdm.WebDriverManager;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.time.Duration;
import java.util.UUID;

@Slf4j
public class WebScreenshotUtils {
	private static final WebDriver webDriver;
	private static final float COMPRESSION_QUALITY = 0.3f;
	
	static {
		final int DEFAULT_WIDTH = 1600;
		final int DEFAULT_HEIGHT = 900;
		
		webDriver = initChromeDriver(DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}
	
	/**
	 * 初始化Chrome浏览器驱动程序
	 * @param width 浏览器窗口宽度
	 * @param height 浏览器窗口高度
	 * @return 返回配置好的WebDriver实例
	 * @throws BusinessException 如果初始化失败则抛出业务异常
	 */
	private static WebDriver initChromeDriver(int width, int height) {
		try {
			// 自动管理 ChromeDriver
			System.setProperty("wdm.chromeDriverMirrorUrl", "https://registry.npmmirror.com/binary" +
				".html?path=chromedriver");
			// 使用WebDriverManager自动管理ChromeDriver版本
			WebDriverManager.chromedriver().setup();
			// 创建Chrome选项配置
			ChromeOptions options = new ChromeOptions();
			// 设置Chrome为无头模式
			options.addArguments("--headless");
			// 禁用GPU加速
			options.addArguments("--disable-gpu");
			// 禁用沙盒模式
			options.addArguments("--no-sandbox");
			// 禁用/dev/shm使用
			options.addArguments("--disable-dev-shm-usage");
			// 设置浏览器窗口大小
			options.addArguments("--window-size=" + width + "," + height);
			// 禁用扩展程序
			options.addArguments("--disable-extensions");
			// 设置User-Agent为Chrome浏览器标准标识
			// 创建带有配置选项的ChromeDriver实例
			options.addArguments("--user-agent=Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 " +
				"(KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36");
			// 设置页面加载超时时间为30秒
			WebDriver driver = new ChromeDriver(options);
			// 设置隐式等待时间为10秒
			driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
			// 返回配置完成的WebDriver实例
			driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
			return driver;
			// 记录初始化失败的错误日志
		} catch (Exception e) {
			// 抛出业务异常，提示初始化失败
			log.error("初始化Chrome浏览器失败", e);
			throw new BusinessException(ErrorCode.SYSTEM_ERROR, "初始化Chrome浏览器失败");
		}
	}
	
	/**
	 * 保存图片到指定路径
	 * @param imageBytes 图片字节数组
	 * @param imagePath  图片保存路径
	 */
	private static void saveImage(byte[] imageBytes, String imagePath) {
		try {
			// 使用FileUtil工具类将字节数组写入指定路径
			FileUtil.writeBytes(imageBytes, imagePath);
		} catch (Exception e) {
			// 记录保存失败的错误日志，包含图片路径和异常信息
			log.error("保存截图失败:{}", imagePath, e);
			// 抛出业务异常，提示保存截图失败
			throw new BusinessException(ErrorCode.SYSTEM_ERROR, "保存截图失败");
		}
	}
	
	/**
	 * 压缩图片的方法
	 * @param originalImagePath 原始图片路径
	 * @param compressedImagePath 压缩后图片保存路径
	 */
	private static void compressImage(String originalImagePath, String compressedImagePath) {
		
		// 使用ImgUtil工具类进行图片压缩
		try {
			// 调用压缩方法，传入原始文件路径、目标文件路径和压缩质量参数
			ImgUtil.compress(FileUtil.file(originalImagePath), FileUtil.file(compressedImagePath),
				COMPRESSION_QUALITY);
		} catch (Exception e) {
			// 记录压缩失败的错误日志，包括原始路径、目标路径和异常信息
			log.error("压缩截图失败:{}->{}", originalImagePath, compressedImagePath, e);
			// 抛出业务异常，提示压缩失败
			throw new BusinessException(ErrorCode.SYSTEM_ERROR, "压缩截图失败");
		}
	}
	
	/**
	 * 等待页面完全加载的方法
	 * @param driver WebDriver实例，用于浏览器操作
	 */
	private static void waitForPageLoad(WebDriver driver) {
		try {
			// 创建一个WebDriverWait实例，设置最长等待时间为10秒
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
			// 使用JavaScriptExecutor检查页面是否完全加载
			// 通过document.readyState属性判断页面加载状态
			wait.until(webDriver -> (JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete");
			// 额外等待2秒，确保页面元素完全渲染
			Thread.sleep(2000);
			// 输出日志信息，表示页面加载完成
			log.info("页面加载完成");
		} catch (Exception e) {
			// 捕获异常并输出错误日志，同时记录异常信息
			log.error("等待页面加载出现异常，继续执行截图", e);
		}
	}
	
	/**
	 * 保存网页截图的方法
	 * @param webUrl 要截图的网页URL
	 * @return 返回压缩后截图的保存路径，如果失败则返回null
	 */
	public static String saveWebPageScreenshot(String webUrl) {
		// 检查网页URL是否为空
		if (StrUtil.isBlank(webUrl)) {
			log.error("网页URL不能为空");
			return null;
		}
		try {
			// 创建以UUID前8位命名的随机目录
			String rootPath =
				System.getProperty("user.dir") + File.separator + "tmp" + File.separator + "screenshots" + File.separator + UUID.randomUUID().toString().substring(0, 8);
			FileUtil.mkdir(rootPath);
			// 定义原始图片文件后缀
			final String IMAGE_SUFFIX = ".png";
			// 生成随机数字作为文件名，并拼接完整保存路径
			String imageSavePath = rootPath + File.separator + RandomUtil.randomNumbers(5) + IMAGE_SUFFIX;
			// 访问网页URL
			webDriver.get(webUrl);
			waitForPageLoad(webDriver);
			// 获取网页截图并保存为字节数组
			byte[] screenshotBytes = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.BYTES);
			saveImage(screenshotBytes, imageSavePath);
			log.info("原始截图保存成功:{}", imageSavePath);
			
			final String COMPRESSION_SUFFIX = "_compressed.png";  // 定义压缩图片文件后缀
			// 生成压缩图片的保存路径
			String compressedImagePath = rootPath + File.separator + RandomUtil.randomNumbers(5) + COMPRESSION_SUFFIX;
			// 压缩图片
			compressImage(imageSavePath, compressedImagePath);
			log.info("压缩截图保存成功:{}", compressedImagePath);
			// 删除原始截图
			FileUtil.del(imageSavePath);
			// 返回压缩后的图片路径
			return compressedImagePath;
		} catch (Exception e) {
			log.error("网页截图失败:{}", webUrl, e);
			return null;
		}
	}
	
	@PreDestroy
	public void destroy() {
		webDriver.quit();
	}
}
