package util;

import java.lang.reflect.Field;

import org.junit.Ignore;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

public class RepeatRunner extends BlockJUnit4ClassRunner
{

	public RepeatRunner(Class<?> klass) throws InitializationError
	{
		super(klass);
	}

	@Override
	protected Description describeChild(FrameworkMethod method)
	{
		if (method.getAnnotation(Repeat.class) != null
				&& method.getAnnotation(Ignore.class) == null)
		{

			return describeRepeatTest(method);
		}
		return super.describeChild(method);
	}

	private Description describeRepeatTest(FrameworkMethod method)
	{
		int times = method.getAnnotation(Repeat.class).value();

		Description desc = Description.createSuiteDescription(testName(method)
				+ "[" + times + "] times", method.getAnnotations());

		for (int i = 0; i < times; ++i)
		{
			desc.addChild(Description.createTestDescription(getTestClass()
					.getJavaClass(), "[" + i + "] " + testName(method)));
		}

		return desc;
	}

	@Override
	protected void runChild(FrameworkMethod method, RunNotifier notifier)
	{

		Description desc = describeChild(method);
		ResetSingleton resetSingle = desc.getAnnotation(ResetSingleton.class);

		// wenn repeat an und ignore aus
		if (method.getAnnotation(Repeat.class) != null
				&& method.getAnnotation(Ignore.class) == null)
		{
			if (resetSingle != null)
			{
				try
				{
					runRepeated(methodBlock(method), desc, notifier,
							resetSingle.type(), resetSingle.field());
				} catch (NoSuchFieldException | SecurityException
						| IllegalArgumentException | IllegalAccessException e)
				{
					e.printStackTrace();
				}
			}
			else
			{
				runRepeated(methodBlock(method), desc, notifier);
			}
			return;
		}

		super.runChild(method, notifier);
	}

	private void runRepeated(Statement methodBlock, Description desc,
			RunNotifier notifier)
	{

		for (Description d : desc.getChildren())
		{

			runLeaf(methodBlock, d, notifier);
		}
	}

	private void runRepeated(Statement methodBlock, Description desc,
			RunNotifier notifier, Class _class, String _field)
			throws NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException
	{

		for (Description d : desc.getChildren())
		{
			Field instance = _class.getDeclaredField(_field);
			instance.setAccessible(true);
			instance.set(null, null);
			runLeaf(methodBlock, d, notifier);
		}
	}
}
