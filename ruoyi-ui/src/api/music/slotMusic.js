import request from '@/utils/request'

// 查询时间段音乐关联列表
export function slotMusic(query) {
  return request({
    url: '/music/list',
    method: 'get',
    params: query
  })
}

// 查询时间段音乐关联详细
export function getMusic(id) {
  return request({
    url: '/music/' + id,
    method: 'get'
  })
}

// 新增时间段音乐关联
export function addMusic(data) {
  return request({
    url: '/music',
    method: 'post',
    data: data
  })
}

// 修改时间段音乐关联
export function updateMusic(data) {
  return request({
    url: '/music',
    method: 'put',
    data: data
  })
}

// 删除时间段音乐关联
export function delMusic(id) {
  return request({
    url: '/music/' + id,
    method: 'delete'
  })
}
