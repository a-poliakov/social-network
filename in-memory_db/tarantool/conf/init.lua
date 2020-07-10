function start()
    -- таблица "пользователи"
    box.schema.space.create('users', { if_not_exists = true })

    -- индексы для поиска по таблице
    box.space.users:create_index('primary', { type = "TREE", unique = true, parts = { 1, 'unsigned' }, if_not_exists = true })
    box.space.users:create_index('first_second_name_idx', { type = 'TREE', unique = false, parts = {4, 'string', 5, 'string' }, if_not_exists = true })
    box.space.users:create_index('first_name_idx', { type = 'TREE', unique = false, parts = { 4, 'string' }, if_not_exists = true })
    box.space.users:create_index('second_name_idx', { type = 'TREE', unique = false, parts = { 5, 'string' }, if_not_exists = true })

    -- тестовые данные для отладки запроса
    box.space.users:insert({1, "admin1", "123", "aaa", "bbb"})
    box.space.users:insert({2, "admin1", "123", "aaa", "bba"})
    box.space.users:insert({3, "admin1", "123", "aaa", "bbc"})
    box.space.users:insert({4, "admin1", "123", "aaa", "bbd"})
    box.space.users:insert({5, "admin1", "123", "aaa", "bdd"})
    box.space.users:insert({6, "admin1", "123", "aaa", "ddd"})
    box.space.users:insert({7, "admin1", "123", "aaa", "eee"})
    box.space.users:insert({8, "admin1", "123", "aaa", "aba"})
    box.space.users:insert({9, "admin1", "123", "aaa", "aaa"})
    box.space.users:insert({10, "admin1", "123", "aaa", "aab"})
    box.space.users:insert({11, "admin1", "123", "baa", "bbb"})
    box.space.users:insert({12, "admin1", "123", "baaaa", "bba"})
    box.space.users:insert({13, "admin1", "123", "baa", "bbc"})
    box.space.users:insert({14, "admin1", "123", "baa", "bbd"})
    box.space.users:insert({15, "admin1", "123", "bbb", "bdd"})
    box.space.users:insert({16, "admin1", "123", "bbb", "ddd"})
    box.space.users:insert({17, "admin1", "123", "abb", "eee"})
    box.space.users:insert({18, "admin1", "123", "bbb", "aba"})
    box.space.users:insert({19, "admin1", "123", "bbb", "aaa"})
    box.space.users:insert({20, "admin1", "123", "bbb", "aab"})
end

-- procedure for search by first name prefix AND second name prefix
-- Param: prefix_first_name - prefix for searching first name by like '%first_name'
-- Param: prefix_second_name - prefix for searching second name by like '%second_name'
-- Param: size - max count of entries in response
function search_by_first_second_name(prefix_first_name, prefix_second_name, size)
    local count = 0
    local result = {}
    for _, tuple in box.space.users.index.first_second_name_idx:pairs(prefix_first_name, { iterator = 'GE' }) do
        if string.startswith(tuple[4], prefix_first_name, 1, -1) and string.startswith(tuple[5], prefix_second_name, 1, -1) then
            table.insert(result, tuple)
            count = count + 1
            if count >= size then
                return result
            end
        end
    end
    return result
end

-- procedure for search by first name prefix
-- Param: prefix - prefix for searching first name by like '%first_name'
function search_by_first_name(prefix)
    local result = {}
    for _, tuple in box.space.users.index.first_name_idx:pairs({ prefix }, { iterator = 'GE' }) do
        if string.startswith(tuple[4], prefix, 1, -1) then
            table.insert(result, tuple)
        end
    end
    return result
end

-- procedure for search by second name prefix
-- Param: prefix - prefix for searching first name by like '%second_name'
function search_by_second_name(prefix)
    local result = {}
    for _, tuple in box.space.users.index.second_name_idx:pairs({ prefix }, { iterator = 'GE' }) do
        if string.startswith(tuple[5], prefix, 1, -1) then
            table.insert(result, tuple)
        end
    end
    return result
end
