import request from '@/utils/request'

// 查询时间段列表
export function listSlot(query) {
  return request({
    url: '/slot/list',
    method: 'get',
    params: query
  })
}

// 查询时间段详细
export function getSlot(slotId) {
  return request({
    url: '/slot/' + slotId,
    method: 'get'
  })
}

// 新增时间段
export function addSlot(data) {
  return request({
    url: '/slot',
    method: 'post',
    data: data
  })
}

// 修改时间段
export function updateSlot(data) {
  return request({
    url: '/slot',
    method: 'put',
    data: data
  })
}

// 删除时间段
export function delSlot(slotId) {
  return request({
    url: '/slot/' + slotId,
    method: 'delete'
  })
}
