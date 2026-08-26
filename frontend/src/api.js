/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
let auth = ''
export function setCredentials(username,password){auth='Basic '+btoa(`${username}:${password}`)}
export async function request(path,options={}){
  const response=await fetch(path,{...options,headers:{'Content-Type':'application/json','Authorization':auth,...options.headers}})
  const body=await response.json().catch(()=>({message:'服务响应异常'}))
  if(!response.ok||body.success===false)throw new Error(body.message||`请求失败 ${response.status}`)
  return body.data
}
export const api={
  catalog:()=>request('/api/catalog'),dashboard:()=>request('/api/dashboard'),records:(module='')=>request('/api/records'+(module?`?module=${module}`:'')),
  create:data=>request('/api/records',{method:'POST',body:JSON.stringify(data)}),update:(id,data)=>request(`/api/records/${id}`,{method:'PUT',body:JSON.stringify(data)}),
  remove:id=>request(`/api/records/${id}`,{method:'DELETE'}),action:(id,action,remark)=>request(`/api/records/${id}/actions`,{method:'POST',body:JSON.stringify({action,remark})}),
  audits:()=>request('/api/admin/audit-logs'),settings:()=>request('/api/admin/settings'),saveSettings:data=>request('/api/admin/settings',{method:'PUT',body:JSON.stringify(data)})
}
