package com.vauff.maunzdiscord.commands;

import com.vauff.maunzdiscord.core.AbstractCommand;
import com.vauff.maunzdiscord.core.Main;
import com.vauff.maunzdiscord.core.Util;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.MessageChannel;
import discord4j.core.object.entity.User;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.HashMap;

public class Changelog extends AbstractCommand<MessageCreateEvent>
{
	private static HashMap<String, Integer> listPages = new HashMap<>();
	private static HashMap<String, String> listMessages = new HashMap<>();
	private static HashMap<String, String> listVersions = new HashMap<>();

	@Override
	public void exe(MessageCreateEvent event, MessageChannel channel, User author) throws Exception
	{
		String[] args = event.getMessage().getContent().get().split(" ");
		Document doc;
		ArrayList<String> changelog = new ArrayList<>();
		String link;
		String title;
		String version;

		if (args.length == 1)
		{
			version = "v" + Main.version;
		}
		else
		{
			if (args[1].startsWith("v"))
			{
				version = args[1];
			}
			else
			{
				version = "v" + args[1];
			}
		}

		link = "https://github.com/Vauff/Maunz-Discord/releases/tag/" + version;
		title = "Maunz " + version;

		try
		{
			doc = Jsoup.connect(link).userAgent(" ").get();
		}
		catch (HttpStatusException e)
		{
			Util.msg(channel, author, "That version of Maunz doesn't exist!");
			return;
		}

		String html = doc.select("div[class=\"markdown-body\"]").html().replace("<strong>", "").replace("</strong>", "").replace("<ul>", "").replace("</ul>", "").replace("<li>", "").replace("</li>", "");
		String[] split = html.split("\n");

		for (int i = 1; i < split.length; i++)
		{
			if (!split[i].replace(" ", "").equals(""))
			{
				changelog.add("-" + split[i]);
			}
		}

		Util.msg(channel, author, "GitHub link: " + "<" + link + ">");

		Message m = Util.buildPage(changelog, title, 10, 1, false, true, channel, author);

		listMessages.put(author.getId().asString(), m.getId().asString());
		listVersions.put(author.getId().asString(), version);
		waitForReaction(m.getId().asString(), author.getId().asString());
		listPages.put(author.getId().asString(), 1);
	}

	@Override
	public void onReactionAdd(ReactionAddEvent event, Message message) throws Exception
	{
		if (listMessages.containsKey(event.getUser().block().getId().asString()))
		{
			if (message.getId().asString().equals(listMessages.get(event.getUser().block().getId().asString())))
			{
				if (event.getEmoji().asUnicodeEmoji().get().getRaw().equals("▶"))
				{
					Document doc = null;
					ArrayList<String> changelog = new ArrayList<>();
					String link;
					String title;

					link = "https://github.com/Vauff/Maunz-Discord/releases/tag/" + listVersions.get(event.getUser().block().getId().asString());
					title = "Maunz " + listVersions.get(event.getUser().block().getId().asString());

					try
					{
						doc = Jsoup.connect(link).userAgent(" ").get();
					}
					catch (HttpStatusException e)
					{
						Util.msg(event.getChannel().block(), event.getUser().block(), "That version of Maunz doesn't exist!");
						return;
					}

					String html = doc.select("div[class=\"markdown-body\"]").html().replace("<strong>", "").replace("</strong>", "").replace("<ul>", "").replace("</ul>", "").replace("<li>", "").replace("</li>", "");
					String[] split = html.split("\n");

					for (int i = 1; i < split.length; i++)
					{
						if (!split[i].replace(" ", "").equals(""))
						{
							changelog.add("-" + split[i]);
						}
					}

					Message m = Util.buildPage(changelog, title, 10, listPages.get(event.getUser().block().getId().asString()) + 1, false, true, event.getChannel().block(), event.getUser().block());

					listMessages.put(event.getUser().block().getId().asString(), m.getId().asString());
					waitForReaction(m.getId().asString(), event.getUser().block().getId().asString());
					listPages.put(event.getUser().block().getId().asString(), listPages.get(event.getUser().block().getId().asString()) + 1);
				}

				else if (event.getEmoji().asUnicodeEmoji().get().getRaw().equals("◀"))
				{
					Document doc = null;
					ArrayList<String> changelog = new ArrayList<>();
					String link;
					String title;

					link = "https://github.com/Vauff/Maunz-Discord/releases/tag/" + listVersions.get(event.getUser().block().getId().asString());
					title = "Maunz " + listVersions.get(event.getUser().block().getId().asString());

					try
					{
						doc = Jsoup.connect(link).userAgent(" ").get();
					}
					catch (HttpStatusException e)
					{
						Util.msg(event.getChannel().block(), event.getUser().block(), "That version of Maunz doesn't exist!");
						return;
					}

					String html = doc.select("div[class=\"markdown-body\"]").html().replace("<strong>", "").replace("</strong>", "").replace("<ul>", "").replace("</ul>", "").replace("<li>", "").replace("</li>", "");
					String[] split = html.split("\n");

					for (int i = 1; i < split.length; i++)
					{
						if (!split[i].replace(" ", "").equals(""))
						{
							changelog.add("-" + split[i]);
						}
					}

					Message m = Util.buildPage(changelog, title, 10, listPages.get(event.getUser().block().getId().asString()) - 1, false, true, event.getChannel().block(), event.getUser().block());

					listMessages.put(event.getUser().block().getId().asString(), m.getId().asString());
					waitForReaction(m.getId().asString(), event.getUser().block().getId().asString());
					listPages.put(event.getUser().block().getId().asString(), listPages.get(event.getUser().block().getId().asString()) - 1);
				}
			}
		}
	}

	@Override
	public String[] getAliases()
	{
		return new String[] { "*changelog" };
	}
}