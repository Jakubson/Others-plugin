ProxyServer.getInstance().getScheduler().runAsync(JPremium.getInstance(), () ->
        {
            Connection connection = UserUtils.checkPremium(nickname);

            if (!connection.isFeedback())
            {
                String preServersNotResponding = ConfigManager.getInstance().getDefaultMessages().getServersNotResponding();
                String serversNotResponding = ChatColor.translateAlternateColorCodes('&', preServersNotResponding);

                event.setCancelReason(TextComponent.fromLegacyText(serversNotResponding));
                event.setCancelled(true);
                event.completeIntent(JPremium.getInstance());
                return;
            }

            if (UserUtils.isUserExits(nickname))
            {
                User user = User.get(nickname);

                if ((user.getIndex() != null) && (!user.isPremium()))
                {
                    if (!user.getNickname().equals(nickname))
                    {
                        String preIncorrectNickname = ConfigManager.getInstance().getMessages(user).getNonPremium().getLogin().getError().getIncorrectNickname();
                        String incorrectNickname = MessageManager.getInstance().replaceVariables(preIncorrectNickname, user).replaceAll("%PLAYER_NAME%", nickname);

                        event.setCancelReason(TextComponent.fromLegacyText(incorrectNickname));
                        event.setCancelled(true);
                        event.completeIntent(JPremium.getInstance());
                        return;
                    }

                    event.getConnection().setOnlineMode(false);
                    event.completeIntent(JPremium.getInstance());
                    return;
                }

                UserUtils.removeUser(user);
            }

            if (connection.isPremium())
            {
                boolean cache = UserUtils.isCacheExits(connection.getUniqueId());
                boolean auto = ConfigManager.getInstance().getSettings().isAutoLoginPremium();

                if (cache || auto)
                {
                    event.getConnection().setOnlineMode(true);
                    event.completeIntent(JPremium.getInstance());
                    return;
                }
            }

            registerByWebsite(nickname, event);
        });
