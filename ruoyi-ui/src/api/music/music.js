import request from '@/utils/request'

// 查询音乐列表
export function listMusic(query) {
  return request({
    url: '/music/list',
    method: 'get',
    params: query
  })
}

// 查询音乐详细
export function getMusic(musicId) {
  return request({
    url: '/music/' + musicId,
    method: 'get'
  })
}

// 新增音乐
export function addMusic(param) {
  return request({
    url: '/music',
    method: 'post',
    params: param,
    // headers: {
    //   'Content-Type': 'multipart/form-data'
    // },
  })
}

// 修改音乐
export function updateMusic(data) {
  return request({
    url: '/music',
    method: 'put',
    data: data,
    headers: {
      'Content-Type': 'multipart/form-data'
    },
  })
}

// 删除音乐
export function delMusic(musicId) {
  return request({
    url: '/music/' + musicId,
    method: 'delete'
  })
}
