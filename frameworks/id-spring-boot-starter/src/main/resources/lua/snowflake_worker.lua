local hashKey = KEYS[1]
local dataCenterIdField = ARGV[1]
local workerIdField = ARGV[2]
local maxId = tonumber(ARGV[3]) or 31

local fields = redis.call('hmget', hashKey, dataCenterIdField, workerIdField)
local dataCenterId = fields[1]
local workerId = fields[2]

if not dataCenterId then
    redis.call('hmset', hashKey, dataCenterIdField, 0, workerIdField, 0)
    return {0, 0}
end

dataCenterId = tonumber(dataCenterId)
workerId = tonumber(workerId)

-- 全部用完重置
if dataCenterId == maxId and workerId == maxId then
    redis.call('hmset', hashKey, dataCenterIdField, 0, workerIdField, 0)
    return {0, 0}
end

-- 递增 workerId
if workerId < maxId then
    workerId = redis.call('hincrby', hashKey, workerIdField, 1)
    return {dataCenterId, workerId}
end

-- workerId 已满，进位到 datacenterId（此时 dataCenterId 必然 < maxId）
dataCenterId = redis.call('hincrby', hashKey, dataCenterIdField, 1)
redis.call('hset', hashKey, workerIdField, 0)
return {dataCenterId, 0}