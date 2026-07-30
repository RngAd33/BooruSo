/**
 * 图片URL代理工具
 * 将外部CDN图片URL转换为本地代理URL，绕过Referer防盗链
 */

// 堆糖CDN域名
const DUITANG_CDN_HOST = 'c-ssl.duitang.com'
// 本地代理前缀
const DUITANG_PROXY_PREFIX = '/duitang-cdn'

/**
 * 转换图片URL为本地代理URL
 * 如果图片来自需要代理的CDN，则替换为本地代理地址
 * @param {string} url - 原始图片URL
 * @returns {string} 转换后的URL
 */
export function proxyImageUrl(url) {
  if (!url) return url
  if (url.includes(DUITANG_CDN_HOST)) {
    return url.replace(`https://${DUITANG_CDN_HOST}`, DUITANG_PROXY_PREFIX)
               .replace(`http://${DUITANG_CDN_HOST}`, DUITANG_PROXY_PREFIX)
  }
  return url
}
