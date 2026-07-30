/**
 * 图源枚举类
 * 保存图源名称和图源代号的双向映射关系
 */
export const SourceEnum = {
  SAFE_BOORU: {
    name: 'Safebooru',
    code: 1
  },
  DUITANG: {
    name: '堆糖',
    code: 2
  }
}

/**
 * 根据图源代号获取图源信息
 * @param {number} code - 图源代号
 * @returns {Object|null} 图源信息对象
 */
export function getSourceByCode(code) {
  for (const key in SourceEnum) {
    if (SourceEnum[key].code === code) {
      return SourceEnum[key]
    }
  }
  return null
}

/**
 * 根据图源名称获取图源信息
 * @param {string} name - 图源名称
 * @returns {Object|null} 图源信息对象
 */
export function getSourceByName(name) {
  for (const key in SourceEnum) {
    if (SourceEnum[key].name === name) {
      return SourceEnum[key]
    }
  }
  return null
}

/**
 * 获取所有图源列表（用于选择框）
 * @returns {Array} 图源列表
 */
export function getSourceList() {
  return Object.values(SourceEnum).map(source => ({
    label: source.name,
    value: source.code
  }))
}

/**
 * 图源对应的搜索路由路径
 */
const SourceRoutes = {
  [SourceEnum.SAFE_BOORU.code]: '/search',
  [SourceEnum.DUITANG.code]: '/search/duitang'
}

/**
 * 根据图源代号获取搜索路由路径
 * @param {number} code - 图源代号
 * @returns {string} 路由路径
 */
export function getSourceSearchRoute(code) {
  return SourceRoutes[code] || '/search'
}

/**
 * 默认图源代号
 */
export const DEFAULT_SOURCE_CODE = SourceEnum.SAFE_BOORU.code
