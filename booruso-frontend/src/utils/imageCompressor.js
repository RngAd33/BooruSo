/**
 * 图片压缩工具类
 * 提供客户端图片压缩功能，与具体图源解耦
 */

/**
 * 压缩图片
 *
 * @param {string} imageUrl - 图片URL
 * @param {Object} options - 压缩选项
 * @param {number} options.maxWidth - 最大宽度（默认800）
 * @param {number} options.maxHeight - 最大高度（默认800）
 * @param {number} options.quality - 压缩质量 0-1（默认0.8）
 * @param {string} options.type - 输出图片类型（默认image/jpeg）
 * @returns {Promise<string>} 压缩后的图片DataURL
 */
export function compressImage(imageUrl, options = {}) {
  return new Promise((resolve, reject) => {
    const {
      maxWidth = 800,
      maxHeight = 800,
      quality = 0.8,
      type = 'image/jpeg'
    } = options

    const img = new Image()
    img.crossOrigin = 'anonymous'
    
    img.onload = () => {
      try {
        const canvas = document.createElement('canvas')
        let { width, height } = img

        // 计算缩放比例
        if (width > maxWidth || height > maxHeight) {
          const ratio = Math.min(maxWidth / width, maxHeight / height)
          width = Math.floor(width * ratio)
          height = Math.floor(height * ratio)
        }

        canvas.width = width
        canvas.height = height

        const ctx = canvas.getContext('2d')
        // 使用更好的图像质量
        ctx.imageSmoothingEnabled = true
        ctx.imageSmoothingQuality = 'high'
        ctx.drawImage(img, 0, 0, width, height)

        const compressedDataUrl = canvas.toDataURL(type, quality)
        resolve(compressedDataUrl)
      } catch (error) {
        reject(new Error('图片压缩失败: ' + error.message))
      }
    }

    img.onerror = () => {
      reject(new Error('图片加载失败，无法压缩'))
    }

    img.src = imageUrl
  })
}

/**
 * 压缩图片并返回Blob对象
 * @param {string} imageUrl - 图片URL
 * @param {Object} options - 压缩选项
 * @returns {Promise<Blob>} 压缩后的图片Blob
 */
export async function compressImageToBlob(imageUrl, options = {}) {
  const dataUrl = await compressImage(imageUrl, options)
  return dataURLToBlob(dataUrl)
}

/**
 * 将DataURL转换为Blob
 * @param {string} dataURL - DataURL字符串
 * @returns {Blob} Blob对象
 */
function dataURLToBlob(dataURL) {
  const arr = dataURL.split(',')
  const mime = arr[0].match(/:(.*?);/)[1]
  const bstr = atob(arr[1])
  let n = bstr.length
  const u8arr = new Uint8Array(n)
  
  while (n--) {
    u8arr[n] = bstr.charCodeAt(n)
  }
  
  return new Blob([u8arr], { type: mime })
}

/**
 * 获取图片尺寸信息
 * @param {string} imageUrl - 图片URL
 * @returns {Promise<Object>} 图片尺寸信息 {width, height}
 */
export function getImageSize(imageUrl) {
  return new Promise((resolve, reject) => {
    const img = new Image()
    img.crossOrigin = 'anonymous'
    
    img.onload = () => {
      resolve({
        width: img.naturalWidth,
        height: img.naturalHeight
      })
    }
    
    img.onerror = () => {
      reject(new Error('图片加载失败'))
    }
    
    img.src = imageUrl
  })
}

/**
 * 判断是否需要压缩
 * @param {string} imageUrl - 图片URL
 * @param {number} threshold - 尺寸阈值（默认1920）
 * @returns {Promise<boolean>} 是否需要压缩
 */
export async function needCompress(imageUrl, threshold = 1920) {
  try {
    const { width, height } = await getImageSize(imageUrl)
    return width > threshold || height > threshold
  } catch {
    return false
  }
}
