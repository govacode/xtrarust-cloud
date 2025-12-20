local hashKey = KEYS[1]
local dataCenterIdField = ARGV[1]
local workerIdField = ARGV[2]

if (redis.call('exists', hashKey) == 0) then
    redis.call('hincrby', hashKey, dataCenterIdField, 0)
    redis.call('hincrby', hashKey, workerIdField, 0)
    return { 0, 0 }
end

local dataCenterId = tonumber(redis.call('hget', hashKey, dataCenterIdField))
local workId = tonumber(redis.call('hget', hashKey, workerIdField))

local max = 31
local resultWorkerId = 0
local resultDataCenterId = 0

if (dataCenterId == max and workId == max) then
    redis.call('hset', hashKey, dataCenterIdField, '0')
    redis.call('hset', hashKey, workerIdField, '0')
elseif (workId ~= max) then
    resultWorkerId = redis.call('hincrby', hashKey, workerIdField, 1)
    resultDataCenterId = dataCenterId
elseif (dataCenterId ~= max) then
    resultWorkerId = 0
    resultDataCenterId = redis.call('hincrby', hashKey, dataCenterIdField, 1)
    redis.call('hset', hashKey, workerIdField, '0')
end

return { resultDataCenterId, resultWorkerId }
