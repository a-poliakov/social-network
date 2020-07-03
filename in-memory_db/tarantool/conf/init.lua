function start()
    box.schema.space.create('user_space', { if_not_exists = true })
    box.space.user_space:create_index('primary', { type = "TREE", unique = true, parts = { 1, 'unsigned' }, if_not_exists = true })
    box.space.user_space:create_index('first_second_name_idx', { type = 'TREE', unique = false, parts = { 4, 'string', 5, 'string' }, if_not_exists = true })
    box.space.user_space:create_index('first_name_idx', { type = 'TREE', unique = false, parts = { 4, 'string' }, if_not_exists = true })
    box.space.user_space:create_index('second_name_idx', { type = 'TREE', unique = false, parts = { 5, 'string' }, if_not_exists = true })
end

function search_by_first_second_name(prefix_first_name, prefix_second_name, size)
    local count = 0
    local result = {}
    for _, tuple in box.space.user_space.index.first_second_name_idx:pairs({ prefix_first_name, prefix_second_name }, { iterator = 'GE' }) do
        if string.startswith(tuple[4], prefix_first_name, 1, -1) and string.startswith(tuple[5], prefix_second_name, 1, -1) then
            table.insert(result, tuple)
            count = count + 1
            if count >= size then
                return result
            end
        else
            break
        end
    end
    return result
end

function search_by_first_name(prefix)
    local result = {}
    for _, tuple in box.space.user_space.index.first_name_idx:pairs({ prefix }, { iterator = 'GE' }) do
        if string.startswith(tuple[4], prefix, 1, -1) then
            table.insert(result, tuple)
        end
    end
    return result
end

function search_by_second_name(prefix)
    local result = {}
    for _, tuple in box.space.user_space.index.second_name_idx:pairs({ prefix }, { iterator = 'GE' }) do
        if string.startswith(tuple[5], prefix, 1, -1) then
            table.insert(result, tuple)
        end
    end
    return result
end
